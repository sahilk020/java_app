/**
 * author: shubhamchauhan
 * 
 *  To do later : 
 *  Remove unnecessary parseInt() used for converting count variable.
 */
$(document).ready(
	function () {
        
		$('#up').change(
			function () {
               
				if ($(this).val()) {
                   //$('input:submit').attr('disabled', false);
                   
                    $("#btnBulkLogoUpload").attr('disabled',false);
                    
                
				}
			}
        );
       
    });
    $(document).ready(
        function () {
            $("input[name='fileName']").on("change", function(){
                   
                    if ($(this).val() != "") {
                       //$('input:submit').attr('disabled', false);
                       
                      
                        $("#btnBulkPanUpload").attr('disabled', false);
                        $("#btnBulkPanDelete").attr('disabled', true);
                        $("#btnBulkTanDelete").attr('disabled', true);
                        $("#btnBulkTanUpload").attr('disabled', false);
                        $("#btnBulkGstDelete").attr('disabled', true);
                        $("#btnBulkGstUpload").attr('disabled', false);
                        $("#btnBulkAoaDelete").attr('disabled', true);
                        $("#btnBulkAoaUpload").attr('disabled', false);
                        
                    }
                    else{
                        $("#btnBulkPanUpload").attr('disabled', true);
                        $("#btnBulkPanDelete").attr('disabled', false);
                        $("#btnBulkTanUpload").attr('disabled', true);
                        $("#btnBulkTanDelete").attr('disabled', false);
                        $("#btnBulkGstUpload").attr('disabled', true);
                        $("#btnBulkGstDelete").attr('disabled', false);
                        $("#btnBulkAoaUpload").attr('disabled', true);
                        $("#btnBulkAoaDelete").attr('disabled', false);
                    }
                }
                
            );
         
           
        });
    function CheckDimension(formId) {
        var checkDimension = formId.attributes['class'].nodeValue;
        //console.log(checkDimension);
        
       if(checkDimension == 'upload up whitelabelUploadImage companyHeaderIcon'){
        var fileUpload = $(".companyHeaderIcon")[0];
   
        //Check whether the file is valid Image.
       
       
     
            //Check whether HTML5 is supported.
            if (typeof (fileUpload.files) != "undefined") {
                //Initiate the FileReader object.
                var reader = new FileReader();
                //Read the contents of Image File.
                reader.readAsDataURL(fileUpload.files[0]);
                reader.onload = function (e) {
                    //Initiate the JavaScript Image object.
                    var image = new Image();
     
                    //Set the Base64 string return from FileReader as source.
                    image.src = e.target.result;
                           
                    //Validate the File Height and Width.
                    image.onload = function () {
                        var height = this.height;
                        var width = this.width;
                        //var nullfile = document.getElementById("upload").innerText;
                        if (width != 66 || height != 46) {
                        //show width and height to user
                      // data.append(); 
                          document.getElementById("width").innerHTML=width;
                           document.getElementById("height").innerHTML=height;
                           document.getElementById("upload").innerText = "Upload document";
                          // $(input).closest('.fileUpload').find(".icon").attr('src','https://image.flaticon.com/icons/svg/136/136549.svg');
                           
                           //src.appendChild(img);
                            alert("Header Logo dimension must be 66*46.");
                            
                            return false;
                        }
                        alert("Uploaded image has valid Height and Width.");
                        return true;
                    };
     
                }
            }
      
        } else if(checkDimension == 'upload up whitelabelUploadImage companyHeaderText'){
        var fileUpload = $(".companyHeaderText")[0];
        
            //var fileUpload =  $(".companyHeaderIcon");
         
            //Check whether the file is valid Image.
           
           
         
                //Check whether HTML5 is supported.
                if (typeof (fileUpload.files) != "undefined") {
                    //Initiate the FileReader object.
                    var reader = new FileReader();
                    //Read the contents of Image File.
                    reader.readAsDataURL(fileUpload.files[0]);
                    reader.onload = function (e) {
                        //Initiate the JavaScript Image object.
                        var image = new Image();
         
                        //Set the Base64 string return from FileReader as source.
                        image.src = e.target.result;
                               
                        //Validate the File Height and Width.
                        image.onload = function () {
                            var height = this.height;
                            var width = this.width;
                            if (width != 148 || height != 46) {
                            //show width and height to user
                               document.getElementById("width").innerHTML=width;
                               document.getElementById("height").innerHTML=height;
                               document.getElementById("upload1").innerText = "Upload document";
                                alert("Header Text dimension must be 148*46.");
                                
                                return false;
                            }
                            alert("Uploaded image has valid Height and Width.");
                            return true;
                        };
         
                    }
                }
            }
            
            else if(checkDimension == 'upload up whitelabelUploadImage companyFooter'){
                var fileUpload = $(".companyFooter")[0];
             
                //Check whether the file is valid Image.
               
               
             
                    //Check whether HTML5 is supported.
                    if (typeof (fileUpload.files) != "undefined") {
                        //Initiate the FileReader object.
                        var reader = new FileReader();
                        //Read the contents of Image File.
                        reader.readAsDataURL(fileUpload.files[0]);
                        reader.onload = function (e) {
                            //Initiate the JavaScript Image object.
                            var image = new Image();
             
                            //Set the Base64 string return from FileReader as source.
                            image.src = e.target.result;
                                   
                            //Validate the File Height and Width.
                            image.onload = function () {
                                var height = this.height;
                                var width = this.width;
                                if (width != 191 || height != 40) {
                                //show width and height to user
                                   document.getElementById("width").innerHTML=width;
                                   document.getElementById("height").innerHTML=height;
                                   document.getElementById("upload2").innerText = "Upload document";
                                    alert("Footer Logo dimension must be 191*40.");
                                    
                                    return false;
                                }
                                alert("Uploaded image has valid Height and Width.");
                                return true;
                            };
             
                        }
                    }
                }
                else if(checkDimension == 'upload up whitelabelUploadImage companyLogout'){
                    var fileUpload = $(".companyLogout")[0];
                 
                    //Check whether the file is valid Image.
                   
                   
                 
                        //Check whether HTML5 is supported.
                        if (typeof (fileUpload.files) != "undefined") {
                            //Initiate the FileReader object.
                            var reader = new FileReader();
                            //Read the contents of Image File.
                            reader.readAsDataURL(fileUpload.files[0]);
                            reader.onload = function (e) {
                                //Initiate the JavaScript Image object.
                                var image = new Image();
                 
                                //Set the Base64 string return from FileReader as source.
                                image.src = e.target.result;
                                       
                                //Validate the File Height and Width.
                                image.onload = function () {
                                    var height = this.height;
                                    var width = this.width;
                                    if (width != 239 || height != 57) {
                                    //show width and height to user
                                       document.getElementById("width").innerHTML=width;
                                       document.getElementById("height").innerHTML=height;
                                       document.getElementById("upload3").innerText = "Upload document";
                                        alert("Logout Logo dimension must be 239*57.");
                                        
                                        return false;
                                    }
                                    alert("Uploaded image has valid Height and Width.");
                                    return true;
                                };
                 
                            }
                        }
                    }
                    else if(checkDimension == 'upload up whitelabelUploadImage companyThankyou'){
                        var fileUpload = $(".companyThankyou")[0];
                     
                        //Check whether the file is valid Image.
                       
                       
                     
                            //Check whether HTML5 is supported.
                            if (typeof (fileUpload.files) != "undefined") {
                                //Initiate the FileReader object.
                                var reader = new FileReader();
                                //Read the contents of Image File.
                                reader.readAsDataURL(fileUpload.files[0]);
                                reader.onload = function (e) {
                                    //Initiate the JavaScript Image object.
                                    var image = new Image();
                     
                                    //Set the Base64 string return from FileReader as source.
                                    image.src = e.target.result;
                                           
                                    //Validate the File Height and Width.
                                    image.onload = function () {
                                        var height = this.height;
                                        var width = this.width;
                                        if (width != 128 || height != 41) {
                                        //show width and height to user
                                           document.getElementById("width").innerHTML=width;
                                           document.getElementById("height").innerHTML=height;
                                           document.getElementById("upload4").innerText = "Upload document";
                                            alert("Thank You Logo dimension must be 128*41.");
                                            
                                            return false;
                                        }
                                        alert("Uploaded image has valid Height and Width.");
                                        return true;
                                    };
                     
                                }
                            }
                        }
                        else if(checkDimension == 'upload up whitelabelUploadImage companyReceiptLogo'){
                            var fileUpload = $(".companyReceiptLogo")[0];;
                         
                            //Check whether the file is valid Image.
                           
                           
                         
                                //Check whether HTML5 is supported.
                                if (typeof (fileUpload.files) != "undefined") {
                                    //Initiate the FileReader object.
                                    var reader = new FileReader();
                                    //Read the contents of Image File.
                                    reader.readAsDataURL(fileUpload.files[0]);
                                    reader.onload = function (e) {
                                        //Initiate the JavaScript Image object.
                                        var image = new Image();
                         
                                        //Set the Base64 string return from FileReader as source.
                                        image.src = e.target.result;
                                               
                                        //Validate the File Height and Width.
                                        image.onload = function () {
                                            var height = this.height;
                                            var width = this.width;
                                            if (width != 98 || height != 98) {
                                            //show width and height to user
                                               document.getElementById("width").innerHTML=width;
                                               document.getElementById("height").innerHTML=height;
                                               document.getElementById("upload5").innerText = "Upload document";
                                                alert("Receipt Icon Logo dimension must be 98*98.");
                                                
                                                return false;
                                            }
                                            alert("Uploaded image has valid Height and Width.");
                                            return true;
                                        };
                         
                                    }
                                }
                            }
                            else if(checkDimension == 'upload up whitelabelUploadImage companyReceiptText'){
                                var fileUpload = $(".companyReceiptText")[0];
                             
                                //Check whether the file is valid Image.
                               
                               
                             
                                    //Check whether HTML5 is supported.
                                    if (typeof (fileUpload.files) != "undefined") {
                                        //Initiate the FileReader object.
                                        var reader = new FileReader();
                                        //Read the contents of Image File.
                                        reader.readAsDataURL(fileUpload.files[0]);
                                        reader.onload = function (e) {
                                            //Initiate the JavaScript Image object.
                                            var image = new Image();
                             
                                            //Set the Base64 string return from FileReader as source.
                                            image.src = e.target.result;
                                                   
                                            //Validate the File Height and Width.
                                            image.onload = function () {
                                                var height = this.height;
                                                var width = this.width;
                                                if (width != 85 || height != 29) {
                                                //show width and height to user
                                                   document.getElementById("width").innerHTML=width;
                                                   document.getElementById("height").innerHTML=height;
                                                   document.getElementById("upload6").innerText = "Upload document";
                                                    alert("Receipt Text Logo dimension must be 85*29.");
                                                    
                                                    return false;
                                                }
                                                alert("Uploaded image has valid Height and Width.");
                                                return true;
                                            };
                             
                                        }
                                    }
                                }
                                else if(checkDimension == 'upload up whitelabelUploadImage companyPg'){
                                    var fileUpload = $(".companyPg")[0];
                                 
                                    //Check whether the file is valid Image.
                                   
                                   
                                 
                                        //Check whether HTML5 is supported.
                                        if (typeof (fileUpload.files) != "undefined") {
                                            //Initiate the FileReader object.
                                            var reader = new FileReader();
                                            //Read the contents of Image File.
                                            reader.readAsDataURL(fileUpload.files[0]);
                                            reader.onload = function (e) {
                                                //Initiate the JavaScript Image object.
                                                var image = new Image();
                                 
                                                //Set the Base64 string return from FileReader as source.
                                                image.src = e.target.result;
                                                       
                                                //Validate the File Height and Width.
                                                image.onload = function () {
                                                    var height = this.height;
                                                    var width = this.width;
                                                    if (width != 280 || height != 40) {
                                                    //show width and height to user
                                                       document.getElementById("width").innerHTML=width;
                                                       document.getElementById("height").innerHTML=height;
                                                       document.getElementById("upload7").innerText = "Upload document";
                                                        alert("PG Logo dimension must be 280*40.");
                                                        
                                                        return false;
                                                    }
                                                    alert("Uploaded image has valid Height and Width.");
                                                    return true;
                                                };
                                 
                                            }
                                        }
                                    }
                                    else if(checkDimension == 'upload up whitelabelUploadImage companyCrm'){
                                        var fileUpload = $(".companyCrm")[0];
                                     
                                        //Check whether the file is valid Image.
                                       
                                       
                                     
                                            //Check whether HTML5 is supported.
                                            if (typeof (fileUpload.files) != "undefined") {
                                                //Initiate the FileReader object.
                                                var reader = new FileReader();
                                                //Read the contents of Image File.
                                                reader.readAsDataURL(fileUpload.files[0]);
                                                reader.onload = function (e) {
                                                    //Initiate the JavaScript Image object.
                                                    var image = new Image();
                                     
                                                    //Set the Base64 string return from FileReader as source.
                                                    image.src = e.target.result;
                                                           
                                                    //Validate the File Height and Width.
                                                    image.onload = function () {
                                                        var height = this.height;
                                                        var width = this.width;
                                                        if (width != 214 || height != 46) {
                                                        //show width and height to user
                                                           document.getElementById("width").innerHTML=width;
                                                           document.getElementById("height").innerHTML=height;
                                                           document.getElementById("upload8").innerText = "Upload document";
                                                            alert("CRM Logo dimension must be 214*46.");
                                                            
                                                            return false;
                                                        }
                                                        alert("Uploaded image has valid Height and Width.");
                                                        return true;
                                                    };
                                     
                                                }
                                            }
                                        }
    
    }
var fileTypes = ['png'];  //acceptable file types
function readURL(input) {
	if (input.files && input.files[0]) {
		var extension = input.files[0].name.split('.').pop().toLowerCase(),  //file extension from input file
			isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types

		if (isSuccess) { //yes
			var reader = new FileReader();
			reader.onload = function (e) {
                if (extension == 'png')
                {
                     $(input).closest('.fileUpload').find(".icon").attr('src','https://image.flaticon.com/icons/svg/136/136523.svg'); 
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
			$(input).closest('.uploadDoc').find(".docErr").fadeIn();
			document.getElementById("up").value = "";
			$(input).val('');
			setTimeout(function () {
				$('.docErr').fadeOut('slow');

			}, 9000);
		}
	}
}
$(document).ready(function () {
    $("#btnBulkLogoDelete").attr('disabled', true);
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

var fileTypesc = ['pdf', 'jpg', 'jpeg', 'png'];  //acceptable file types
function readURL1(input) {
	if (input.files && input.files[0]) {
		var extension = input.files[0].name.split('.').pop().toLowerCase(),  //file extension from input file
			isSuccess = fileTypesc.indexOf(extension) > -1;  //is extension in acceptable types

		if (isSuccess) { //yes
			var reader = new FileReader();
			reader.onload = function (e) {
                if (extension == 'pdf'){
                	$(input).closest('.fileUpload1').find(".icon").attr('src','https://image.flaticon.com/icons/svg/179/179483.svg');
                }
                 
                else if (extension == 'png'){ 
					$(input).closest('.fileUpload1').find(".icon").attr('src','https://image.flaticon.com/icons/svg/136/136523.svg'); 
                }
                else if (extension == 'jpg' || extension == 'jpeg'){
                	$(input).closest('.fileUpload1').find(".icon").attr('src','https://image.flaticon.com/icons/svg/136/136524.svg');
                }
            

				else {
					//debugger;
					//console.log('here=>'+$(input).closest('.uploadDoc').length);
					$(input).val('');
					document.getElementById("up1").value = "";
					//$("#up").replaceWith($("#up").val('').clone(true));
					$(input).closest('.uploadDoc1').find(".docErr1").slideUp('slow');


				}
			}

			reader.readAsDataURL(input.files[0]);
		}
		else {
			//debugger;
			//console.log('here=>'+$(input).closest('.uploadDoc').find(".docErr").length);
			$(input).closest('.uploadDoc1').find(".docErr1").fadeIn();
			document.getElementById("up1").value = "";
			$(input).val('');
			setTimeout(function () {
				$('.docErr1').fadeOut('slow');

			}, 9000);
		}
	}
}
$(document).ready(function () {

	$(document).on('change', '.up1', function () {
		var id = $(this).attr('id'); /* gets the filepath and filename from the input */
		var profilePicValue = $(this).val();
		var fileNameStart = profilePicValue.lastIndexOf('\\'); /* finds the end of the filepath */
		profilePicValue = profilePicValue.substr(fileNameStart + 1).substring(0, 20); /* isolates the filename */
		//var profilePicLabelText = $(".upl"); /* finds the label text */
		if (profilePicValue != '') {
			//console.log($(this).closest('.fileUpload').find('.upl').length);
			$(this).closest('.fileUpload1').find('.upl1').html(profilePicValue); /* changes the label text */
		}
	});


	$(document).on("click", "a.btn-check", function () {
		if ($(".uploadDoc1").length > 1) {
			$(this).closest(".uploadDoc1").remove();
		} else {
			alert("You have to upload at least one document.");
		}
	});
});
function uploadWhiteLabelDocs(){
   let i = 0; 
    $(".whitelabelForm").each(function(){
        var whitelabelDocKey = $(this).children().children("input").eq(1).val();
        var name = $(this).children().children("input").eq(0).val();
        var emailId = document.getElementById("emailId").value;

        var form = $('#' + $(this).attr("id"))[0];
        var data = new FormData(form);
        // debugger;
        data.append("emailId", emailId);
        data.append("docCategoryType", whitelabelDocKey); 
        data.append("name",name.replace(/.*(\/|\\)/, '').split(' ').join('').split("/").pop().split(".")[0]);
        data.append("extension","."+name.replace(/.*(\/|\\)/, '').split('.').pop())
       i++;
        data.append("token", document.getElementsByName("token")[0].value);

        return $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: 'tenantDocsAddAction',
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            async: true,
            success: function (data) {
               // alert(data.response);
                document.getElementById("listLogo").innerHTML=data.response;
                if(data.docCategoryType == "HEADERICONLOGO"){
                    $("#imageHeaderIcon").html(data.name); 
   
                   }
                   if(data.docCategoryType == "HEADERTEXTLOGO"){
                    $("#imageHeaderText").html(data.name); 
   
                   }
                   
				if(data.docCategoryType == "FOOTERLOGO"){
                 $("#imageFotter").html(data.name); 

                }
                if(data.docCategoryType == "LOGOUTLOGO"){
                    $("#imageLogout").html(data.name); 
   
                   }
                   if(data.docCategoryType == "THANKYOULOGO"){
                    $("#imageThankYou").html(data.name); 
   
                   }
                   if(data.docCategoryType == "RECEIPTICONLOGO"){
                    $("#imageReceiptIcon").html(data.name); 
   
                   }
                   if(data.docCategoryType == "RECEIPTTEXTLOGO"){
                    $("#imageReceiptText").html(data.name); 
   
                   }
                   if(data.docCategoryType == "PGLOGO"){
                    $("#imagePgLogo").html(data.name); 
   
                   }
                   if(data.docCategoryType == "CRMLOGO"){
                    $("#imageCrmLogo").html(data.name); 
   
                   }
			$('#logoUploadDocs').modal('show');
			$("#btnBulkLogoDelete").attr('disabled', true);
            },
            error: function (data) {
                //alert(data.response);
               // console.log("error");
               // console.log(data.response);
            }
        });
 
    });

}

function bulkLogoUpload(docCategoryType, formId,obj) {
    event.preventDefault();
    var emailId = document.getElementById("emailId").value;
    var form = $('#' + formId)[0];
    var data = new FormData(form);
    var name = obj.parentElement.children[0].value;
    namearray = name.split('\\');
    finalName = namearray[namearray.length-1];

   // var name = $(this).children().children("input").eq(0).val();
    // debugger;
    data.append("emailId", emailId);
    data.append("docCategoryType", docCategoryType);
    data.append("name",finalName.split(' ').join('').split("/").pop().split(".")[0]);
    data.append("extension","."+finalName.split('.').pop())
    // if(docCategoryType == "COMPANYPANIMAGE"){
    //     data.append("fileName", document.getElementsByName("fileName1")[0].value); 
       
    // }
    // if( docCategoryType =="COMPANYTANIMAGE"){
    //     data.append("fileName", document.getElementsByName("fileName2")[0].value); 
    //     data.append("name",document.getElementsByName("fileName2")[0].value.replace(/.*(\/|\\)/, '').split(' ').join('').split("/").pop().split(".")[0]);
    //     data.append("extension","."+document.getElementsByName("fileName2")[0].value.replace(/.*(\/|\\)/, '').split('.').pop())
    // }
    // if(docCategoryType == "COMPANYGSTIMAGE"){
    //     data.append("fileName", document.getElementsByName("fileName3")[0].value); 
    //     data.append("name",document.getElementsByName("fileName3")[0].value.replace(/.*(\/|\\)/, '').split(' ').join('').split("/").pop().split(".")[0]);
    //     data.append("extension","."+document.getElementsByName("fileName3")[0].value.replace(/.*(\/|\\)/, '').split('.').pop())

    // }
    // if(docCategoryType == "COMPANYAOAIMAGE"){
    //     data.append("fileName", document.getElementsByName("fileName4")[0].value); 
    //     data.append("name",document.getElementsByName("fileName4")[0].value.replace(/.*(\/|\\)/, '').split(' ').join('').split("/").pop().split(".")[0]);
    //     data.append("extension","."+document.getElementsByName("fileName4")[0].value.replace(/.*(\/|\\)/, '').split('.').pop())
        
    // }

   
    data.append("token", document.getElementsByName("token")[0].value);

    //s data.append("path", path);	

    return $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: 'tenantDocsAddAction',
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        async: true,
        success: function (data) {
            if (data.response == null) {
                alert("Invalid file, please try again ");
            } else {
                alert(data.response);
                //document.getElementsByName("fileName")[0].innerHTML = "";
              //  document.getElementsByName("fileName1")[0].innerHTML = "";
            }
            //alert(data.response);
         //   window.location.reload();
        },
        error: function (data) {
            alert(data.response);
        }
    });



}
function deleteDirectorImage(docCategoryType){
    var confirmationFlag = confirm("Do you want to delete this Image");
     if(!confirmationFlag){
        return false;
     }
     $.ajax({
        url : 'tenantDocsDeleteAction',
        type : 'post',
        data : {
            token : document.getElementsByName("token")[0].value,
            fileName : document.getElementsByName("fileName")[0].value,
            emailId : document.getElementById("emailId").value, 
            docCategoryType : docCategoryType,
           
            
        
            
        },
        success: function (data) {
            if (data.response == null) {
               // console.log("Invalid file, please try again" );
               // alert("Invalid file, please try again ");
            } else {
                //console.log("success");
                alert(data.response);
              // console.log(data.response);
                //document.getElementsByName("fileName")[0].innerHTML = "";
               // document.getElementsByName("fileName1")[0].innerHTML = "";
            }
            alert(data.response);
           // window.location.reload();
        },
        error: function (data) {
            alert(data.response);
           // console.log("error");
           // console.log(data.response);
        }
    });

}
function deleteBulkImage(){
    let i = 0; 
    $(".whitelabelForm").each(function(){
    var whitelabelDocKey = $(this).children().children("input").eq(1).val();
   // var confirmationFlag = confirm("Do you want to delete this Image");
    var form = $('#' + $(this).attr("id"))[0];
        var data = new FormData(form);
	// if(!confirmationFlag){
    //     return false;
    // }
    
	
		$.ajax({
			url : 'tenantDocsDeleteAction',
			type : 'post',
			data : {
                token : document.getElementsByName("token")[0].value,
                fileName : document.getElementsByName("whiteLabelFileName"+ i)[0].value,
				emailId : document.getElementById("emailId").value, 
                docCategoryType : whitelabelDocKey,
                i : i++,
                
            
                
			},
            success: function (data) {
                if (data.response == null) {
                   // console.log("Invalid file, please try again" );
                   // alert("Invalid file, please try again ");
                } else {
                    //console.log("success");
                   // alert(data.response);
                  // console.log(data.response);
                    //document.getElementsByName("fileName")[0].innerHTML = "";
                   // document.getElementsByName("fileName1")[0].innerHTML = "";
                }
                //alert(data.response);
               // window.location.reload();
            },
            error: function (data) {
                //alert(data.response);
               // console.log("error");
               // console.log(data.response);
            }
        });
    });
}

function _ValidateField(){
    setTimeout(function(){

   
    var _ValidCompanyDetails = false;


   
        if( 
        (document.getElementById("tanentNo").value && document.getElementById("tanentNo").className != "textFL_merch_invalid") 
        && (document.getElementById("cname").value && document.getElementById("cname").className != "textFL_merch_invalid") 
        && (document.getElementById("emailId").value && document.getElementById("emailId").className != "textFL_merch_invalid") 
        && (document.getElementById("phone").value && document.getElementById("phone").className != "textFL_merch_invalid") 
        &&  (document.getElementById("city").value && document.getElementById("city").className != "textFL_merch_invalid")
        &&  (document.getElementById("state").value && document.getElementById("state").className != "textFL_merch_invalid")
        &&  (document.getElementById("country").value && document.getElementById("country").className != "textFL_merch_invalid")
		&&  (document.getElementById("zip").value && document.getElementById("zip").className != "textFL_merch_invalid")
		&&  (document.getElementById("websiteUrl").value && document.getElementById("websiteUrl").className != "textFL_merch_invalid")
		&&  (document.getElementById("address").value && document.getElementById("address").className != "textFL_merch_invalid")
		&&  (document.getElementById("comGst").value && document.getElementById("comGst").className != "textFL_merch_invalid")
        &&  (document.getElementById("tanNumber").value && document.getElementById("tanNumber").className != "textFL_merch_invalid")
		&&  (document.getElementById("accHolderName").value && document.getElementById("accHolderName").className != "textFL_merch_invalid")
        &&  (document.getElementById("cin").value && document.getElementById("cin").className != "textFL_merch_invalid")
        &&  (document.getElementById("pgUrl").value && document.getElementById("pgUrl").className != "textFL_merch_invalid")
        
	
        ){
            
            _ValidCompanyDetails = true;
        }else{
            _ValidCompanyDetails = false;
        }		

    if( _ValidCompanyDetails){
        document.getElementById("companybtnsave").disabled = false;
        document.getElementById("kycbtnsave").disabled = false;
    }else{
        document.getElementById("companybtnsave").disabled = true;
        document.getElementById("kycbtnsave").disabled = true;
    }
},200);
}


class FieldValidator {
    constructor(x) {
        //this.x = x;
    }


    static valdTanentNo(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[A-Za-z]{6}[0-9]{4}/;
        var tanentElement = document.getElementById("tanentNo");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("tanentNo", "Enter valid Tanent no.", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('tanentNo');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("tanentNo", "Please enter Invoice No.", errMsgFlag);
            return false;
        }
    }
    static valdHsnSac(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp =  /^[0-9]{4,8}$/;
        var tanentElement = document.getElementById("hsnSac");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("hsnSac", "Enter valid HSNSac Code.", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('hsnSac');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("hsnSac", "Please enter HSNSac Code.", errMsgFlag);
            return false;
        }
	}
	static valdCin(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[0-9a-zA-Z\b\:/-\s\.]{2,30}$/;
        var tanentElement = document.getElementById("cin");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("cin", "Enter valid CIN ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('cin');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("cin", "Please enter CIN", errMsgFlag);
            return false;
        }
	}
	static valdAccName(errMsgFlag){
		var tanentexp = /^[a-zA-Z ]{4,200}$/;
        var tanentElement = document.getElementById("accHolderName");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("accHolderName", "Enter valid Account Name ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('accHolderName');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("accHolderName", "Please enter Account Name", errMsgFlag);
            return false;
        }

	}
	
	static valdTanNumber(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[0-9a-zA-Z\b\:/-\s\.]{2,30}$/;
        var tanentElement = document.getElementById("tanNumber");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("tanNumber", "Enter valid TAN Number ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('tanNumber');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("tanNumber", "Please enter TAN", errMsgFlag);
            return false;
        }
	}
	

	static valdCompGst(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[ A-Za-z0-9]*$/
        var tanentElement = document.getElementById("comGst");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("comGst", "Enter valid Company GST ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('comGst');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("comGst", "Please enter GST", errMsgFlag);
            return false;
        }
	}


    static valdPhoneNo(errMsgFlag) {
        var phoneElement = document.getElementById("phone");
        var value = phoneElement.value.trim();
        if (value.length > 0) {
            var phone = phoneElement.value;
            var phoneexp = /^[0-9]{8,13}$/;
            if (!phone.match(phoneexp)) {
                FieldValidator.addFieldError("phone", "Enter valid phone no.", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                //this._ValidateField();
                FieldValidator.removeFieldError('phone');
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("phone", "Please enter Phone No.", errMsgFlag);
            //FieldValidator.removeFieldError('phone');
            return true;
        }
	}


    static valdCurrCode(errMsgFlag) {
        var currencyCodeElement = document.getElementById("currencyCode");
        if (currencyCodeElement.value == "Select Currency") {
            FieldValidator.addFieldError("currencyCode", "Select Currency Type", errMsgFlag)
            return false;
        } else {
            FieldValidator.removeFieldError('currencyCode');
            return true;
        }

    }
    

  

    static valdMerchant(errMsgFlag) {
        var element = document.getElementById("merchant")
        if ((element) != null) {
            if (element.value != "Select Merchant") {
                FieldValidator.removeFieldError('merchant');
                return true;
            } else {
                FieldValidator.addFieldError("merchant", "Select Merchant", errMsgFlag)
                return false;
            }
        } else {
            return true;
        }
    }

    

    //valdiating the amount of the product
  
   

    static valdEmail(errMsgFlag) {
        var emailRegex =  /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
       // var emailRegex = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
        //var emailRegex = /[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?/;
        var element = document.getElementById("emailId");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!value.match(emailRegex)) {
                FieldValidator.addFieldError('emailId', "Enter valid email address", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                FieldValidator.removeFieldError('emailId');
                //this._ValidateField();
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("emailId", "Please Enter Email Id", errMsgFlag)
            //FieldValidator.removeFieldError('emailId');

            return true;
        }
    }

    static valdCompName(errMsgFlag) {
        var nameRegex = /^[0-9a-zA-Z\b\:@;#()/-\s\.]{2,256}$/;
        var element = document.getElementById("cname");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('cname', "Enter valid company name", errMsgFlag);
               
                //this._ValidateField();
                return false;
            } else {
               
                //this._ValidateField();
                FieldValidator.removeFieldError('cname');
                return true;
            }
        } else {
            
            //this._ValidateField();
            FieldValidator.addFieldError("cname", "Please Enter Name", errMsgFlag)
            //FieldValidator.removeFieldError('name');
            return true;
        }
    }
    static valdCountry(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,100}$/;
        var element = document.getElementById("country");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('country', "Enter valid country", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('country');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('country');
            return true;
        }
    }
    static valdCity(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,100}$/;
        var element = document.getElementById("city");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('city', "Enter valid city", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('city');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('city');
            return true;
        }
    }
    static valdState(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,100}$/;
        var element = document.getElementById("state");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('state', "Enter valid state", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('state');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('state');
            return true;
        }
    }

    static valdZip(errMsgFlag) {
        var zipRegex = "^[a-zA-Z0-9]{6,10}$";
        var element = document.getElementById("zip");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(zipRegex)) {
                FieldValidator.addFieldError('zip', 'Enter valid zip code', errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('zip');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('zip');
            return true;
        }
    }

    static valdWebsiteUrl(errMsgFlag) {
        var urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        var element = document.getElementById("websiteUrl");
        var value = element.value.trim();

        if (value.length > 0) {
            if (!(value).match(urlRegex)) {
                FieldValidator.addFieldError('websiteUrl', "Enter valid return url", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('websiteUrl');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('websiteUrl');
            return true;
        }
    }
    
    static valdPgUrl(errMsgFlag) {
        var urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        var element = document.getElementById("pgUrl");
        var value = element.value.trim();

        if (value.length > 0) {
            if (!(value).match(urlRegex)) {
                FieldValidator.addFieldError('pgUrl', "Enter valid return url", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('pgUrl');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('pgUrl');
            return true;
        }
	}
	

    static valdAddress(errMsgFlag) {
        var addRegex = /^([a-zA-Z0-9()-/.,@;:#\r\n\s]){3,150}$/; //-/() .,@;:# \r\n
        var element = document.getElementById("address");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(addRegex).test(value)) {
                FieldValidator.addFieldError('address', "Enter valid address", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('address');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('address');
            return true;
        }

    }

   


    static valdAllFields() {

        var flag = FieldValidator.valdMerchant(true);


        
            flag = flag && FieldValidator.valdTanentNo(true);
            flag = flag && FieldValidator.valdHsnSac(true);
			flag = flag && FieldValidator.valdCin(true);
			falg = falg && FieldValidator.valdCompGst(true);
            flag = flag && FieldValidator.valdCompName(true);
            flag = flag && FieldValidator.valdCity(true);
            flag = flag && FieldValidator.valdAddress(true);
            flag = flag && FieldValidator.valdCountry(true);
            flag = flag && FieldValidator.valdState(true);
            flag = flag && FieldValidator.valdZip(true);
            flag = flag && FieldValidator.valdPhoneNo(true);
            flag = flag && FieldValidator.valdEmail(true);
            flag = flag && FieldValidator.valdWebsiteUrl(true);
			flag = flag && FieldValidator.valdAccName(true);
			
            
        if (flag) {
            document.forms["frmTanent"].submit();
        }
    }

    //to show error in the fields
    static addFieldError(fieldId, errMsg, errMsgFlag) {
        var errSpanId = fieldId + "Err";
        var elmnt = document.getElementById(fieldId);
        elmnt.className = "textFL_merch_invalid";
        elmnt.focus();
        if (errMsgFlag) {
            document.getElementById(errSpanId).innerHTML = errMsg;
        }
    }

    // to remove the error 
    static removeFieldError(fieldId) {
        var errSpanId = fieldId + "Err";
        document.getElementById(errSpanId).innerHTML = "";
        document.getElementById(fieldId).className = "textFL_merch";
    }
}
function _ValidateBankField(){
    setTimeout(function(){

   
    var _ValidBankDetails = false;


   
        if(  (document.getElementById("ifscCode").value && document.getElementById("ifscCode").className != "textFL_merch_invalid")
		&&  (document.getElementById("bankName").value && document.getElementById("bankName").className != "textFL_merch_invalid")
		&&  (document.getElementById("branchName").value && document.getElementById("branchName").className != "textFL_merch_invalid")
		&&  (document.getElementById("panCard").value && document.getElementById("panCard").className != "textFL_merch_invalid")
		&&  (document.getElementById("panName").value && document.getElementById("panName").className != "textFL_merch_invalid")
		&&  (document.getElementById("accountNo").value && document.getElementById("accountNo").className != "textFL_merch_invalid")
        ){
            
            _ValidBankDetails = true;
        }else{
            _ValidBankDetails = false;
        }		


    if( _ValidBankDetails){
        document.getElementById("bankbtnsave").disabled = false;
    }else{
        document.getElementById("bankbtnsave").disabled = true;
    }
},200);
}


class FieldValidatorBank {
    constructor(x) {
        //this.x = x;
    }


   
	static valdBranch(errMsgFlag){
		var tanentexp = /^[0-9a-zA-Z ]{3,200}$/;
        var tanentElement = document.getElementById("branchName");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidatorBank.addFieldError("branchName", "Enter valid Branch Name ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidatorBank.removeFieldError('branchName');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidatorBank.addFieldError("branchName", "Please enter Branch Name", errMsgFlag);
            return false;
        }

	}
	
	static valdBankName(errMsgFlag) {
        var bankNameexp = /^[a-zA-Z\b\,-\s\_.]{1,250}$/;
        var bankNameElement = document.getElementById("bankName");
        var value = bankNameElement.value.trim();
        if (value.length > 0) {
            if (!(value).match(bankNameexp)) {
                FieldValidatorBank.addFieldError('bankName', "Enter valid Bank Name", errMsgFlag);
                this.valdname = true;
                // this._ValidateField();
                return false;
            } else {
                this.valdname = false;
                // this._ValidateField();
                FieldValidatorBank.removeFieldError('bankName');
                return true;
            }
        } else {
            this.valdname = true;
            // this._ValidateField();
            FieldValidatorBank.addFieldError("bankName", "Please enter Bank Name", errMsgFlag)
            // FieldValidator.removeFieldError('name');
            return true;
        }
	}
	static valdIfsc(errMsgFlag) {
        var ifscElement = document.getElementById("ifscCode");
        var value = ifscElement.value.trim();
        if (value.length > 0) {
            var ifsc = ifscElement.value;
            var ifscexp = /^[A-Za-z]{4}[0-9]{7}$/;
            if (!ifsc.match(ifscexp)) {
                FieldValidatorBank.addFieldError("ifscCode", "Please enter valid IFSC Code", errMsgFlag);
                return false;
            } else {
                FieldValidatorBank.removeFieldError('ifscCode');
                return true;
            }
        } else {
            FieldValidatorBank.removeFieldError('ifscCode');
            return true;
        }
    }
	
	static valdAccountNo(errMsgFlag) {
        var phoneElement = document.getElementById("accountNo");
        var value = phoneElement.value.trim();
        if (value.length > 0) {
            var phone = phoneElement.value;
            var phoneexp = /^[0-9]{6,200}$/;
            if (!phone.match(phoneexp)) {
                FieldValidatorBank.addFieldError("accountNo", "Enter valid account no.", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                //this._ValidateField();
                FieldValidatorBank.removeFieldError('accountNo');
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidatorBank.addFieldError("accountNo", "Please enter account No.", errMsgFlag);
            //FieldValidator.removeFieldError('phone');
            return true;
        }
    }

    

    static valdPan(errMsgFlag) {
        var productDescElement = document.getElementById("panCard");
        var value = productDescElement.value.trim();
        if (value.length > 0) {
            var productDesc = productDescElement.value;
            var regex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}/;
            if (!productDesc.match(regex)) {
                FieldValidatorBank.addFieldError("panCard", "Enter valid PAN Card", errMsgFlag);
                return false;
            } else {
                FieldValidatorBank.removeFieldError('panCard');
                return true;
            }
        } else {
            FieldValidatorBank.removeFieldError('panCard');
            return true;
        }
	}
	
    static valdPanName(errMsgFlag) {
        var productDescElement = document.getElementById("panName");
        var value = productDescElement.value.trim();
        if (value.length > 0) {
            var productDesc = productDescElement.value;
            var regex = /^[a-zA-Z ]{2,30}$/;
            if (!productDesc.match(regex)) {
                FieldValidatorBank.addFieldError("panName", "Enter valid PAN Name ", errMsgFlag);
                return false;
            } else {
                FieldValidatorBank.removeFieldError('panName');
                return true;
            }
        } else {
            FieldValidatorBank.removeFieldError('panName');
            return true;
        }
	}
	

    static valdCurrCode(errMsgFlag) {
        var currencyCodeElement = document.getElementById("currencyCode");
        if (currencyCodeElement.value == "Select Currency") {
            FieldValidatorBank.addFieldError("currencyCode", "Select Currency Type", errMsgFlag)
            return false;
        } else {
            FieldValidatorBank.removeFieldError('currencyCode');
            return true;
        }

    }
    

  

    static valdMerchant(errMsgFlag) {
        var element = document.getElementById("merchant")
        if ((element) != null) {
            if (element.value != "Select Merchant") {
                FieldValidatorBank.removeFieldError('merchant');
                return true;
            } else {
                FieldValidatorBank.addFieldError("merchant", "Select Merchant", errMsgFlag)
                return false;
            }
        } else {
            return true;
        }
    }

    

    
	
    static valdAllFields() {

        var flag = FieldValidatorBank.valdMerchant(true);


        
			
            flag = flag && FieldValidatorBank.valdAccountNo(true);
            flag = flag && FieldValidatorBank.valdBranch(true);
            flag = flag && FieldValidatorBank.valdPan(true);
            flag = flag && FieldValidatorBank.valdCurrCode(true);
			flag = flag && FieldValidatorBank.valdPanName(true);
			flag = flag && FieldValidatorBank.valdIfsc(true);
			flag = flag && FieldValidatorBank.valdBankName(true);
			flag = flag && FieldValidatorBank.valdPgUrl(true);
	
            
        if (flag) {
            document.forms["frmTanent"].submit();
        }
    }

    //to show error in the fields
    static addFieldError(fieldId, errMsg, errMsgFlag) {
        var errSpanId = fieldId + "Err";
        var elmnt = document.getElementById(fieldId);
        elmnt.className = "textFL_merch_invalid";
        elmnt.focus();
        if (errMsgFlag) {
            document.getElementById(errSpanId).innerHTML = errMsg;
        }
    }

    // to remove the error 
    static removeFieldError(fieldId) {
        var errSpanId = fieldId + "Err";
        document.getElementById(errSpanId).innerHTML = "";
        document.getElementById(fieldId).className = "textFL_merch";
    }
}
function _ValidateKYCField(){
    setTimeout(function(){

   
    var _ValidKYCDetails = false;


   
        if(  (document.getElementById("panCardKYC").value && document.getElementById("panCardKYC").className != "textFL_merch_invalid")
		&&  (document.getElementById("mobileNokyc").value && document.getElementById("mobileNokyc").className != "textFL_merch_invalid")
		&&  (document.getElementById("kycName").value && document.getElementById("kycName").className != "textFL_merch_invalid")
		&&  (document.getElementById("emailIdkyc").value && document.getElementById("emailIdkyc").className != "textFL_merch_invalid")
		&&  (document.getElementById("aadhar").value && document.getElementById("aadhar").className != "textFL_merch_invalid")
        ){
            
            _ValidKYCDetails = true;
        }else{
            _ValidKYCDetails = false;
        }		


    if( _ValidKYCDetails){
		document.getElementById("addDirector").disabled = false;
		document.getElementById("kycbtnsave").disabled = false;
    }else{
		document.getElementById("addDirector").disabled = true;
		document.getElementById("kycbtnsave").disabled = true;
    }
},200);
}


class FieldValidatorKYC {
    constructor(x) {
        //this.x = x;
    }

	static kycEmailId(errMsgFlag) {

		var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
		var emailElement = document.getElementById("emailIdkyc");
		var emailValue = emailElement.value;
		if (emailValue.trim() !== "") {
			if (!emailValue.match(emailexp)) {
				FieldValidatorKYC.addFieldError("emailIdkyc", "Enter valid Email Id ", errMsgFlag);
				return false;
			} else {
				
				FieldValidatorKYC.removeFieldError('emailIdkyc');
				return true;
			}
		} else {
			emailElement.focus();
			FieldValidatorKYC.addFieldError("emailIdkyc", "Please enter email id", errMsgFlag);
	
			return true;
		}
	}
	static valdPanKYC(errMsgFlag) {
        var productDescElement = document.getElementById("panCardKYC");
        var value = productDescElement.value.trim();
        if (value.length > 0) {
            var productDesc = productDescElement.value;
            var regex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}/;
            if (!productDesc.match(regex)) {
                FieldValidatorKYC.addFieldError("panCardKYC", "Enter valid PAN Card", errMsgFlag);
                return false;
            } else {
                FieldValidatorKYC.removeFieldError('panCardKYC');
                return true;
            }
        } else {
            FieldValidatorKYC.removeFieldError('panCardKYC');
            return true;
        }
	}
	static valdKycPhoneNo(errMsgFlag) {
		var phoneElement = document.getElementById("mobileNokyc");
		var value = phoneElement.value.trim();
		if (value.length > 0) {
			var phone = phoneElement.value;
			var phoneexp = /^[0-9]{8,13}$/;
			if (!phone.match(phoneexp)) {
				FieldValidatorKYC.addFieldError("mobileNokyc", "Enter valid Phone No ", errMsgFlag);
	
				return false;
			} else {
				FieldValidatorKYC.removeFieldError('mobileNokyc');
				return true;
			}
		} else {
			phoneElement.focus();
			FieldValidatorKYC.addFieldError("mobileNokyc", "Please enter Mobile Number", errMsgFlag);
			return true;
		}
	}
	
	static valdAadhar(errMsgFlag) {
		var aadharElement = document.getElementById("aadhar");
		var value = aadharElement.value.trim();
		if (value.length > 0) {
			var aadhar = aadharElement.value;
			var aadharexp = /^[0-9]{12,12}$/;
			if (!aadhar.match(aadharexp)) {
				FieldValidatorKYC.addFieldError("aadhar", "Enter valid Aadhar ", errMsgFlag);
			   
				return false;
			} else {
				FieldValidatorKYC.removeFieldError('aadhar');
				return true;
			}
		} else {
			FieldValidatorKYC.addFieldError("aadhar", "Please enter Aadhar", errMsgFlag);
			return true;
		}
	}
	static valdKYCName(errMsgFlag){
		var tanentexp = /^[a-zA-Z ]{4,200}$/;
        var tanentElement = document.getElementById("kycName");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidatorKYC.addFieldError("kycName", "Enter valid  Name ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidatorKYC.removeFieldError('kycName');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidatorKYC.addFieldError("kycName", "Please enter  Name", errMsgFlag);
            return false;
        }

	}
   
	
  

    static valdMerchant(errMsgFlag) {
        var element = document.getElementById("merchant")
        if ((element) != null) {
            if (element.value != "Select Merchant") {
                FieldValidatorKYC.removeFieldError('merchant');
                return true;
            } else {
                FieldValidatorKYC.addFieldError("merchant", "Select Merchant", errMsgFlag)
                return false;
            }
        } else {
            return true;
        }
    }

    

    
	
    static valdAllFields() {

        var flag = FieldValidatorKYC.valdMerchant(true);


        
			
            flag = flag && FieldValidatorKYC.valdKYCName(true);
            flag = flag && FieldValidatorKYC.valdAadhar(true);
            flag = flag && FieldValidatorKYC.valdPanKYC(true);
            flag = flag && FieldValidatorKYC.kycEmailId(true);
			flag = flag && FieldValidatorKYC.valdKycPhoneNo(true);
	
            
        if (flag) {
            document.forms["frmTanent"].submit();
        }
    }

    //to show error in the fields
    static addFieldError(fieldId, errMsg, errMsgFlag) {
        var errSpanId = fieldId + "Err";
        var elmnt = document.getElementById(fieldId);
        elmnt.className = "textFL_merch_invalid";
        elmnt.focus();
        if (errMsgFlag) {
            document.getElementById(errSpanId).innerHTML = errMsg;
        }
    }

    // to remove the error 
    static removeFieldError(fieldId) {
        var errSpanId = fieldId + "Err";
        document.getElementById(errSpanId).innerHTML = "";
        document.getElementById(fieldId).className = "textFL_merch";
    }
}
var pageReloaded = false;
$(document).ready(function() {
    $("#addDirector").attr("disabled", true);
    document.getElementById("kycbtnsave").disabled = true;
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

function saveTenantEditDetails(){
    
    let tenantNumber = document.getElementById("tanentNo").value;
    let companyName = document.getElementById("cname").value;
    let companyUrl = document.getElementById("websiteUrl").value;
    let pgUrl = document.getElementById("pgUrl").value;
    let ifscCode = document.getElementById("ifscCode").value;
    let bankName = document.getElementById("bankName").value;
    let panName = document.getElementById("panName").value;
    let cin = document.getElementById("cin").value;
    let currency = document.getElementById("currencyCode").value;
    let hsnSacCode = document.getElementById("hsnSac").value;
    //let payId = document.getElementById("merchantPayId").value;
    // let expiryDate = document.getElementById("expiryexpiryDateDate").value;
    let companyGstNo = document.getElementById('comGst').value;
    let tanNumber = document.getElementById("tanNumber").value;
    let accHolderName = document.getElementById("accHolderName").value;
    let mobile = document.getElementById("phone").value;
    let emailId = document.getElementById("emailId").value;
    let city = document.getElementById("city").value;
    let state = document.getElementById("state").value;
    let country = document.getElementById("country").value;
    let postalCode = document.getElementById("zip").value;
    let address = document.getElementById("address").value;
    let panCard = document.getElementById("panCard").value;
    let branchName = document.getElementById("branchName").value;
    let accountNo = document.getElementById("accountNo").value;
    let tenantStatus = document.getElementById("status").value;
    var acquirer = document.getElementById("selectBox1").title;
   // var fileInput = document.getElementById('up');
//     var fd = new FormData();
// var files = $('#up')[0].files[0];
// fd.append('file',files);
// console.log(files);
    //   var file = fileInput.files[0];
    //   var formData = new FormData();
    //   formData.append('file', file);
    //   console.log(formData);
    //   console.log(file);


    var token = document.getElementsByName("token")[0].value;

    if(acquirer==''){
        acquirer='ALL'
    }
    
    
    directorList;
    var directorname = "";
    var directorAadharNumber = "";
    var directorpan = "";
    var directorMobileNumber = "";
    var directorEmail = "";
   
    
    if(directorList != null ){
        var i;
        for (i = 0; i < directorList.length; i++) {
        	directorname += directorList[i].directorEmail + ",";
        	directorAadharNumber += directorList[i].directorAadharNumber + ",";
        	directorpan += directorList[i].directorpan + ",";
        	directorMobileNumber += directorList[i].directorMobileNumber + ",";
        	directorEmail += directorList[i].directorEmail + ",";
        }
        
        directorname 		 = directorname.slice(0, -1);
        directorAadharNumber = directorAadharNumber.slice(0, -1);
        directorpan          = directorpan.slice(0, -1);
        directorMobileNumber = directorMobileNumber.slice(0, -1);
        directorEmail        = directorEmail.slice(0, -1);    	
    }
    if(directorList == null || directorList == ""){
        alert("Please Add Director Details");
        return false;
    }

    
  //  debugger;
    document.getElementById("pgbtnsave").disabled = true;
    document.getElementById("companybtnsave").disabled = true;
    document.getElementById("bankbtnsave").disabled = true;
    document.getElementById("kycbtnsave").disabled = true;
    $('#loader-wrapper').show();
    $.ajax({
        type: "POST",
        timeout: 0,
        url: "editTenantSaveAction",
        data: {
            "tenantNumber": tenantNumber,
            "tenantStatus" :tenantStatus,
            "companyName" : companyName,
            "ifscCode": ifscCode,
            "cin": cin,
            "currency": currency,
            "hsnSacCode": hsnSacCode,
            "branchName": branchName,
            "accHolderName": accHolderName,
            "bankName": bankName,
            "panCard" : panCard,
            "panName": panName,
            "companyUrl": companyUrl,
            "pgUrl": pgUrl,
            "acquirerString":acquirer,
            "tanNumber": tanNumber,
            "companyGstNo": companyGstNo,
            "country":country,
            "mobile": mobile,
            "emailId": emailId,
            "city": city,
            "state": state,
            "postalCode" : postalCode,
            "address" : address,
            "accountNo" : accountNo,
            "directorname":directorname,
            "directorAadharNumber":directorAadharNumber,
            "directorpan":directorpan,
            "directorMobileNumber":directorMobileNumber,
            "directorEmail":directorEmail,
            "token": token,
            "struts.token.name": "token",
        },
        success: function(data) {
            //console.log(data);
            document.getElementById("tenadd").innerHTML=data.response;
            $('#modalAddTenant').modal('show');
            $('#loader-wrapper').hide();
            document.getElementById("pgbtnsave").disabled = false;
            document.getElementById("companybtnsave").disabled = false;
            document.getElementById("bankbtnsave").disabled = false;
            document.getElementById("kycbtnsave").disabled = false;

        },
        error: function(data) {
            $('#modalAddTenantError').modal('show');
            $('#loader-wrapper').hide();
            document.getElementById("pgbtnsave").disabled = false;
            document.getElementById("companybtnsave").disabled = false;
            document.getElementById("bankbtnsave").disabled = false;
            document.getElementById("kycbtnsave").disabled = false;
        }
    });

}






