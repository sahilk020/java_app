var OffusList = [];
var sid;
var sid1;


var Offus = function(merchant, currency, payment_type, mop, transaction_type, acquirer, onUsFlag , cardHolderType ,paymentsRegion ){
    this.merchant = merchant;
    this.currency = currency;
    this.paymentType = payment_type;
    this.mopType = mop;
    this.transactionType = transaction_type;
    this.acquirerMap = acquirer;
    this.onUsFlag = onUsFlag;
    this.payId = merchant;
	this.cardHolderType = cardHolderType;
	this.paymentsRegion = paymentsRegion;
};

var getOffUs = function(){

	console.log("Inside of getOffUs::::")
	var ou_row;
	var ou_currency = [];
    var ou_txnType = [];
    var ou_paymentType = [];
    var ou_mopType = [];
    var ou_Acquirer = [];
    var errormessage = "";
    var acquirerMessage = "";
    var CurrentOffusListLength;
    var match;
    var matchNumber = 0;
    var onUsFlag = true;
    var OffusListTemp = [];
    var merchant;
    var payId;
	var cardHolderType;
	var paymentsRegion;

    if($('#offus_section input[name="selectedCurrency"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Currency\n";
    }else{
        $('#offus_section input[name="selectedCurrency"]:checked').each(function(){

            ou_currency.push($(this).val());
        });
    }

    if($('#offus_section input[name="txnType"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Transection Type\n";
    }else{
        $('#offus_section input[name="txnType"]:checked').each(function(){
            ou_txnType.push($(this).val());
        });
    }

    if($('#offus_section input[name="paymentType"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Payment Type\n";
    }else{
        $('#offus_section input[name="paymentType"]:checked').each(function(){
            ou_paymentType.push($(this).val());
        });
    }

    if($('#offus_section input[name="mopType"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Transection Mop Type\n";
    }else{
        $('#offus_section input[name="mopType"]:checked').each(function(){
            ou_mopType.push($(this).val());
        });
    }

    if($('.AcquirerList input[name^="Acquirer"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Acquirer\n";
    } else if( $('.AcquirerList input[name^="Acquirer"]:checked').length < $('.AcquirerList > [class^="Acquirer"]').length ){
		acquirerMessage = "Please Choose Acquirer preference or remove unused preference!";
    } else{
    	var acq_pref_count = 1;
        $('.AcquirerList input[name^="Acquirer"]:checked').each(function(){
            ou_Acquirer.push(acq_pref_count+"-"+$(this).val());
            //alert(ou_Acquirer);
            acq_pref_count++;
        });

    }


   	merchant = $('#offus_merchant').val();





	// SET REGION
	if (!document.getElementById("regionINTERNATIONAL").checked && !document.getElementById("regionDOMESTIC").checked ){
		alert("Select a Region");
		return false;
	}

	if (document.getElementById("regionINTERNATIONAL").checked ){
		paymentsRegion = "INTERNATIONAL";
	}

	if (document.getElementById("regionDOMESTIC").checked ){
		paymentsRegion = "DOMESTIC";
	}


	// SET
	if (!document.getElementById("typeCardCONSUMER").checked && !document.getElementById("typeCardCOMMERCIAL").checked ){
		alert("Select a Type");
		return false;
	}

	if (document.getElementById("typeCardCONSUMER").checked ){
		cardHolderType = "CONSUMER";
	}

	if (document.getElementById("typeCardCOMMERCIAL").checked ){
		cardHolderType = "COMMERCIAL";
	}


    if(errormessage != ""){
    	swal({
    		title:"",
    		text: errormessage
		});
    } else if (acquirerMessage != ""){
    	swal({
    		title:"",
    		text: acquirerMessage
		});
    } else {

        CurrentOffusListLength = OffusList.length;

    	for(var ou_currency_key = 0; ou_currency_key < ou_currency.length ; ou_currency_key ++ ){

            for(var ou_paymentType_key = 0; ou_paymentType_key < ou_paymentType.length; ou_paymentType_key ++){

                for(var ou_mopType_key = 0; ou_mopType_key < ou_mopType.length; ou_mopType_key ++){

                    for(var ou_txnType_key = 0; ou_txnType_key < ou_txnType.length ; ou_txnType_key ++ ){

                    	onUsFlag = false;

                        ou_row = new Offus(merchant, ou_currency[ou_currency_key], ou_paymentType[ou_paymentType_key], ou_mopType[ou_mopType_key], ou_txnType[ou_txnType_key], ou_Acquirer.join(', '), onUsFlag , cardHolderType ,paymentsRegion);

                        match = false;
                        for(var a = 0; a < CurrentOffusListLength; a++){
                        	if( ( ou_row.merchant === OffusList[a].merchant || ou_row.merchant != "ALL MERCHANTS" && OffusList[a].merchant == "ALL MERCHANTS" ) && (ou_row.currency === OffusList[a].currency) && (ou_row.paymentType === OffusList[a].paymentType) && (ou_row.mopType === OffusList[a].mopType) && (ou_row.transactionType === OffusList[a].transactionType) && (ou_row.onUsFlag === OffusList[a].onUsFlag) && (ou_row.cardHolderType === OffusList[a].cardHolderType) && (ou_row.paymentsRegion === OffusList[a].paymentsRegion) )
                        	{
                        		match = true;
                        		matchNumber++;
                        	}
                        }
                    	if(match == false){
                            OffusListTemp.push(ou_row);
                    	}

                    }

                }

            }

        }

    	$('#offus_section input[type="checkbox"]:checked').removeAttr('checked');

    }


    if(matchNumber > 0){
    	swal({
    		type: "info",
    		title: matchNumber + " Match already exist!",
    		type: "info"
		});
    }

    if(OffusListTemp.length > 0){
    	var listData= {values:OffusListTemp};
		var token  = document.getElementsByName("token")[0].value;
        var data1= "";
		data1 = data1.concat("{" , "\"", "listData","\"",":", JSON.stringify(OffusListTemp),",\"","token","\":\"",token,"\"",",\"","num","\":\"",1,"\"","}" );




        $.ajax({
			type : "POST",
			url : "onusoffusRulesSetup",
			timeout : 0,
			dataType : "json",
			contentType: "application/json; charset=utf-8",
			data :data1,
			success : function(data) {
				console.log(data);
				if(OffusListTemp.length > 0){
					var response = data.response;
					var flag = data.flag;
					if(flag){
						error = "success"
					}else{
						error = "error";
					}
					swal({
					  title: response,
					  type: error,
					}, function () {
						  window.location.reload();
					});
		        }
			},
			error : function(data) {
				window.location.reload();
			}
		})
    }

};

/////////////////////////////////////////////
/////////Get On Us Functions Start Here////////
/////////////////////////////////////////////


var getOnUs = function(){
	var ou_row;
	var ou_currency = [];
    var ou_txnType = [];
    var ou_paymentType = [];
    var ou_mopType = [];
    var ou_Acquirer = [];
    var errormessage = "";
    var CurrentOffusListLength;
    var match;
    var matchNumber = 0;
    var onUsFlag = true;
    var OffusListTemp = [];
    var merchant;

    if($('#onus_section input[name="currency"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Currency\n";
    }else{
        $('#onus_section input[name="currency"]:checked').each(function(){
            ou_currency.push($(this).val());
        });
    }

    if($('#onus_section input[name="txnType"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Transection Type\n";
    }else{
        $('#onus_section input[name="txnType"]:checked').each(function(){
            ou_txnType.push($(this).val());
        });
    }

    if($('#onus_section input[name="paymentType"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Payment Type\n";
    }else{
        $('#onus_section input[name="paymentType"]:checked').each(function(){
            ou_paymentType.push($(this).val());
        });
    }

    if($('#onus_section input[name="mopType"]:checked').length < 1){
        errormessage = errormessage + "Please choose at least one Transection Mop Type\n";
    }else{
        $('#onus_section input[name="mopType"]:checked').each(function(){
            ou_mopType.push($(this).val());
        });
    }

    if( ($('#onus_section input[name="acquirer"]:checked').length < 1)){
        errormessage = errormessage + "Please choose at least one Acquirer\n";
    }else{
        $('#onus_section input[name="acquirer"]:checked').each(function(){
        	ou_Acquirer.push($(this).val());
        });
    }

    merchant = $('#onus_merchant').val();

    if(errormessage != ""){
    	swal({
    		title:"",
    		text: errormessage
		});
    } else {

        CurrentOffusListLength = OffusList.length;

    	for(var ou_currency_key = 0; ou_currency_key < ou_currency.length ; ou_currency_key ++ ){

            for(var ou_paymentType_key = 0; ou_paymentType_key < ou_paymentType.length; ou_paymentType_key ++){

                for(var ou_mopType_key = 0; ou_mopType_key < ou_mopType.length; ou_mopType_key ++){

                    for(var ou_txnType_key = 0; ou_txnType_key < ou_txnType.length ; ou_txnType_key ++ ){

                    	for(var ou_Acquirer_key = 0; ou_Acquirer_key < ou_Acquirer.length ; ou_Acquirer_key ++ ){

                    		ou_row = new Offus(merchant, ou_currency[ou_currency_key], ou_paymentType[ou_paymentType_key], ou_mopType[ou_mopType_key], ou_txnType[ou_txnType_key], ou_Acquirer[ou_Acquirer_key], onUsFlag)
                            match = false;
                            for(var a = 0; a < CurrentOffusListLength; a++){
                            	if(  ( ou_row.merchant === OffusList[a].merchant || ou_row.merchant != "ALL MERCHANTS" && OffusList[a].merchant == "ALL MERCHANTS" ) && (ou_row.currency === OffusList[a].currency) && (ou_row.paymentType === OffusList[a].paymentType) && (ou_row.mopType === OffusList[a].mopType) && (ou_row.transactionType === OffusList[a].transactionType) && (ou_row.acquirerMap === OffusList[a].acquirerMap) && (ou_row.onUsFlag === OffusList[a].onUsFlag) )
                            	{
                            		match = true;
                            		matchNumber++;
                            	}
                            }
                        	if(match == false){
                                OffusListTemp.push(ou_row);
                        	}

                    	}

                	}

                }

            }

        }

    	$('#onus_section input:checked').removeAttr('checked');

    }

    if(matchNumber > 0){
    	swal({
    		title:"",
    		text: matchNumber + " Match found in records!",
    		type: "info"
		});
    }

    if(OffusListTemp.length > 0){
    	var listData= {values:OffusListTemp};
		var token  = document.getElementsByName("token")[0].value;
        var data1= "";
		data1 = data1.concat("{" , "\"", "listData","\"",":", JSON.stringify(OffusListTemp),",\"","token","\":\"",token,"\"",",\"","num","\":\"",4,"\"","}" );

        $.ajax({
			type : "POST",
			url : "onusoffusRulesSetup",
			timeout : 0,
			dataType : "json",
			contentType: "application/json; charset=utf-8",
			data :data1,

			success : function(data) {
				console.log(data);
				if(OffusListTemp.length > 0){
					var response = data.response;
					swal({
					  title: response,
					  type: "success"
					}, function () {
						  window.location.reload();
					});
		        }
			},
			error : function(data) {
				window.location.reload();
			}
		})
    }

};

/////////////////////////////////////////////
/////////Get On Us Functions End Here////////
/////////////////////////////////////////////



//    function removeRow(index){
//          function(data) {
//				var response = data.response;
//
//				alert('Rule Deleted!');
//			},
//			error : function(data) {
//				window.location.reload();
//			}
//		});
//    	$(this).parent().parent().remove();
//    }

 $(document).ready(function(){


	$("#acquirerFinal").select2({
		placeholder: "Select Acquirer",
		allowClear: true
	});
$("#acquirerFinal").on("select2:select", function (evt) {
		var element = evt.params.data.element;
		var $element = $(element);
		  $element.detach();
		$(this).append($element);

		$(this).trigger("change");

		var selectedAcquirer = $("#acquirerFinal").val();
		console.log("Acquirer");
		$("#selectedAcquirerList").val('').change();
		for(var i=0;i<selectedAcquirer.length;i++){
			selectedAcquirer[i] = (i+1) +"-"+ selectedAcquirer[i];
		}
		console.log(selectedAcquirer.join(','));
		$("#selectedAcquirerList").val(selectedAcquirer.join(','));

	});

	$("#acquirerFinal").on("select2:unselect",function(evt){
		console.log("UnSelect");
		$("#selectedAcquirerList").val('').trigger('change');
		var selectedAcquirer = $("#acquirerFinal").val();

		for(var i=0;i<selectedAcquirer.length;i++){
			selectedAcquirer[i] = (i+1) +"-"+ selectedAcquirer[i];
		}
		console.log(selectedAcquirer.join(','));
		$("#selectedAcquirerList").val(selectedAcquirer.join(','));


	});
	$('.card-list-toggle').on('click', function(){
		$(this).toggleClass('active');
		$(this).next('.card-list').slideToggle();
	});


	// $("#acquirerFinal").on('change',function(){
	// 	var selectedAcquirer=$("#acquirerFinal").val();
	// 	var selectedList=$("#acquirerFinal option:selected");
	// 	$("#selectedAcquirerList").val('');

	// 	console.log("Selected List");
	// 	console.log(selectedList);
	// 	for(var i=0;i<selectedAcquirer.length;i++){
	// 		selectedAcquirer[i] = (i+1) +"-"+ selectedAcquirer[i];
	// 	}
	// 	console.log(selectedAcquirer.join(','));
	// 	$("#selectedAcquirerList").val(selectedAcquirer.join(','));


	// });
	$("#selectMerchant").on('change', function(){

		{
			//document.getElementById("blok").style.display ="none";


			var typ =1;
			var merchantVal = document.getElementById("selectMerchant").value;

		    $.ajax({

			type : "GET",
			url : "getdata",
			timeout : 0,
			data : {

				"type":typ,
					"payId":merchantVal,
					"struts.token.name": "token",
				},

			success : function(data1) {
				console.log("Edit acq 1 :"+data1.acquirerListEdit);
				OnusList = data1.acquirerListEdit;
	  			{

	  			var acqHtml1 ="";
	  		localStorage.clear();
	  			var length = data1.acquirerListEdit ? data1.acquirerListEdit.length : 0;
	  			for(var itm =0; itm< length;itm++)
	  			{

	  			  acqHtml1 = acqHtml1+"<div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='checkbox' width='100%'><input type='radio'   onclick='getpaymenttype1(this.value)' id='acquirer1"+data1.acquirerListEdit[itm]+"' name='Acquirer1' value='"+data1.acquirerListEdit[itm]+"' ><label for='Acquirer1"+data1.acquirerListEdit[itm]+"'>"+data1.acquirerListEdit[itm]+"</label></div></div>"

	  			}

	  			document.getElementById("acqdivraw").innerHTML=acqHtml1;

	  			var det = document.getElementById("acqdivraw")
	  			localStorage.clickcountraw=det ;
	  			 var det1 = $(".AcquirerListraw").html();
	  			localStorage.clickcountraw=det1 ;


  			}
	  		},
	  		error : function(data) {
	  			window.location.reload();
	  		}



			});

		}



		document.getElementById("loading").style.display = "block";
		$(".onus_table").hide();
		    $('.offus_table').empty();
			$('.onus_table').empty();
		//	document.getElementById("tohide").style.display ="none";
		var merchantVal = document.getElementById("selectMerchant").value;
		    if(merchantVal == "" || merchantVal == "Select Merchant" || merchantVal == null){
				alert("Please Select Merchant");
				document.getElementById("loading").style.display = "none";
				return false;
			}

		$.ajax({
		type : "GET",
		url : "getRulesList",
		timeout : 0,
		data : {
				"payId":merchantVal,
				"struts.token.name": "token",
			},
		success : function(data) {
			document.getElementById("loading").style.display = "none";
			document.getElementById("onUs_default").classList.remove('active');

	OffusList = data.routerRules;
			console.log(data.routerRules);
			if(OffusList.length > 0){
				acqH="<input type='checkbox' class='selectAllcountry' id='myCheck' disabled='disabled' name='deleteRule' onClick='toggle(this)' value=Select All'/><label>Select All</label"
					butt="<button type='button' id='but' class='btn btn-danger' disabled='disabled' name='deleteRule' style='position:relative;left:900px;top:-8px;'onclick='myFun()' >Delete ALL</button>"

			document.getElementById("del").innerHTML=acqH;
		document.getElementById("del1").innerHTML=butt;

		         for(var i = 0; i < OffusList.length; i++){
		        	 if(OffusList[i].onUsFlag == false){
				         $('.offus_table').show();
		        		 $('.offus_table').append('<tr class="boxtext"><td><input type="checkbox" class="chkCountry" onclick="mychecktest()" id="chbox" name="sport" value="'+OffusList[i].id+'"></td><td align="left" valign="middle">' + OffusList[i].merchant + '</td><td class="dataPay" align="left" valign="middle" data-currency="'+OffusList[i].currency+'"	>' + OffusList[i].currency + '</td><td align="left" valign="middle" data-paymentType="'+OffusList[i].paymentType+'">' + OffusList[i].paymentType + '</td><td align="left" valign="middle">' + OffusList[i].mopType + '</td><td align="left" valign="middle">' + OffusList[i].transactionType + '</td><td align="left" valign="middle">' + OffusList[i].acquirerMap + '<div class="AcquirerListTemp_'+OffusList[i].id+'"></div></td><td  align="left" valign="middle" >' + OffusList[i].paymentsRegion + '</td><td align="left" valign="middle"> ' + OffusList[i].cardHolderType + '</td><td align="left" valign="top">' + '<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal" id="editModel" onClick="editMethod(this)" data-id="'+OffusList[i].id+'" >&times; Edit</button> &nbsp; <button type="button" class="btn btn-danger remove-row" disabled="disabled" name="deleteRule" data-id="'+OffusList[i].id+'">&times; Remove</button></td></tr>')
		        	 }else{
						 document.getElementById("loading").style.display = "none";

				         $('.onus_table').show();
		        		 $('.onus_table').append('<tr class="boxtext"><td align="left" valign="middle">' + OffusList[i].merchant + '</td><td align="left" valign="middle">' + OffusList[i].acquirerMap + '</td><td align="left" valign="middle">' + OffusList[i].currency + '</td><td align="left" valign="middle">' + OffusList[i].paymentType + '</td><td align="left" valign="middle">' + OffusList[i].mopType + '</td><td align="left" valign="middle">' + OffusList[i].transactionType + '</td><td align="left" valign="top">' + '<button type="button" disabled="disabled" name="deleteRule" class="btn btn-danger remove-row" data-id="'+OffusList[i].id+'">&times; Remove</button></td></tr>')
		        	 }
		         }

		     }
		     enbaleButtonAsPerAccess();
		},
		error : function(data) {
			window.location.reload();
		}
	    });
	});
	 $('#offus_section input').click(function(){
	    	if($('#offus_section input:checked').length > 0){
	    		$('.offus_table').addClass('disabled');
	    		$('#offus_reset').removeClass('disabled');
	    		if( ($('#offus_section input[name="selectedCurrency"]').is(':checked')) && ($('#offus_section input[name="txnType"]').is(':checked')) && ($('#offus_section input[name="paymentType"]').is(':checked')) && ($('#offus_section input[name="mopType"]').is(':checked')) && ($('.AcquirerList input[name^="Acquirer"]').is(':checked')) ){
	    			$('#offus_submit').removeClass('disabled');
	    		}else{
	    			$('#offus_submit').addClass('disabled');
	    		}
	    	}else{
	    		$('.offus_table').removeClass('disabled');
	    		$('#offus_reset').addClass('disabled');
	    	}
	    });



//	 $('#offus_section input').click(function(){
//    	if($('#offus_section input:checked').length > 0){
//	debugger
//    		//$('.offus_table').addClass('disabled');
//    		$('#offus_reset').removeClass('disabled');
//    		if( ($('#offus_section input[name="currency"]').is(':checked')) && ($('#acqdiv1 input[name^="Acquirer"]').is(':checked'))) {
//    			$('#offus_submit').removeClass('disabled');
//    		}else{
//    			$('#offus_submit').addClass('disabled');
//    		}
//    	}else{
//    		$('.offus_table').removeClass('disabled');
//    		$('#offus_reset').addClass('disabled');
//    	}
//    });
//

	    $("#onus_merchant").on('change', function(){

		document.getElementById("dvmop").style.display ="none";

document.getElementById("dvpy").style.display ="none";


			document.getElementById("loading").style.display = "block";
			//$("#onUs_default").hide();
			    $('.offus_table').empty();
				$('.onus_table').empty();
				var typ =1;
			var merchantVal = document.getElementById("onus_merchant").value;
			    if(merchantVal == "" ||  merchantVal == null){
					alert("Please Select Merchant");
					document.getElementById("loading").style.display = "none";
					return false;
				}


		    $.ajax({
			type : "GET",
			url : "getdata",
			timeout : 0,
			data : {
			"type":typ,
				"payId":merchantVal,
				"struts.token.name": "token",
			},

		success : function(data) {


			var i;

			document.getElementById("loading").style.display = "none";
			//document.getElementById("onUs_default").classList.remove('active');

			var acqHtml ="";
			for(var itm =0; itm< data.acquirerList.length;itm++)
			{acqHtml = acqHtml+"<div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='checkbox' width='100%'><input type='radio'   onclick='sel(this.value)' name='acquirer' value='"+data.acquirerList[itm]+"' id='acquirer'><label for='Acquir"+data.acquirerList[itm]+"'>"+data.acquirerList[itm]+"</label></div></div>"
			  //acqHtml = acqHtml+"<label><div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='wwctrl' width='100%'><input type='radio'   onclick='sel(this.value)' name='acquirer' value='"+data.acquirerList[itm]+"' id='acquirer'><input type='hidden' id='__checkbox_acquirer' name='acquirer' value='"+data.acquirerList[itm]+"'>"+data.acquirerList[itm]+"</div> </div></label>"
			}
			//var element = document.getElementById("dvAcq");
			document.getElementById("dvAcq").innerHTML=acqHtml;

			},
			error : function(data) {
				window.location.reload();
			}
		    });
		});

		  $("#offus_merchant").on('change', function(){




		  		document.getElementById("mopid1").style.display = "none";
		  		document.getElementById("loading").style.display = "block";
		  		document.getElementById("paytype1").style.display = "none";

		  		    	var typ =1;
		  		var merchantVal = document.getElementById("offus_merchant").value;
		  		    if(merchantVal == "" ||  merchantVal == null){
		  				alert("Please Select Merchant");
		  				document.getElementById("loading").style.display = "none";
		  				return false;
		  			}


		  	    $.ajax({

		  		type : "GET",
		  		url : "getdata",
		  		timeout : 0,
		  		data : {

		  			"type":typ,
		  				"payId":merchantVal,
		  				"struts.token.name": "token",
		  			},

		  		success : function(data) {


		  			var i;
		  			document.getElementById("loading").style.display = "none";

		  			{

		  			var acqHtml1 ="";
		  		//	localStorage.clear();

		  			let acquirerSize = data.acquirerList? data.acquirerList.length:0;
		  			for(var itm =0; itm< acquirerSize;itm++)
		  			{

		  			  acqHtml1 = acqHtml1+"<div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='checkbox' width='100%'><input type='radio'   onclick='getCurrency(this.value)' id='acquirer1"+data.acquirerList[itm]+"' name='Acquirer1' value='"+data.acquirerList[itm]+"' ><label for='Acquirer1"+data.acquirerList[itm]+"'>"+data.acquirerList[itm]+"</label></div></div>"

		  			}

		  			document.getElementById("acqdiv").innerHTML=acqHtml1;

		  			var det = document.getElementById("acqdiv")
		  			localStorage.clickcount=det ;
		  			 var det1 = $(".AcquirerList").html();
		  			localStorage.clickcount=det1 ;
		  			}
		  		},
		  		error : function(data) {
		  			window.location.reload();
		  		}
		  	    });
		  	}	);



	$('.product-spec').on('click', '.remove-row', function(events){
	    var index = $(this).attr('data-id');

		var token  = document.getElementsByName("token")[0].value;
		swal({
			title: "Are you sure want to delete this Rule?",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "Yes, delete it!",
			closeOnConfirm: false
			}, function (isConfirm) {
		if (!isConfirm) return;
	    $.ajax({
			type : "POST",
			url : "deleteRouterRule",
			timeout : 0,
			data : {
				"id":index,
				"token":token,
				"struts.token.name": "token",
			},
			success : function(data) {
				var response = data.response;
				swal({
				 title: 'Rule Deleted!',
				 type: "success"
				}, function(){
					$(this).closest('tr').remove();
					window.location.reload();
				});
			},
			error : function(data) {
				window.location.reload();
			}
		});
		});
	});



 	var acquirerCount = 1;
	var checkCount = 0;
	var checkedAcquirer = [];
	var acquirerCopy="";


	var acquirerRemoveBtn = '<button type="button" class="btn btn-danger acquirerRemoveBtn">Remove</button>';
	var cloneIndex = $(".AcquirerList > [class^='Acquirer']").length;
	var AQflag = false;

	$('.AcquirerList + .acquirerCloneBtn').on('click', function(){
		debugger;
		var acquirerCopy=  localStorage.clickcount;

		checkedAcquirer.push($('.AcquirerList input[name="Acquirer'+cloneIndex+'"]:checked').val());
		cloneIndex++;
		var acquirers = $($(acquirerCopy).find('label'));
		var ogAcqs = [];
		for(var i=1;i<acquirers.length;i++){
			ogAcqs.push($(acquirers[i]).text());
		}
		var newAcqs = [];
		for (var i = 0; i < ogAcqs.length; i++) {
			newAcqs.push(ogAcqs[i].replace(/1/g, cloneIndex));
		}
		acquirerClone = acquirerCopy.replace(/1/g, cloneIndex);
		for (var i = 0; i < newAcqs.length; i++) {
			acquirerClone = acquirerClone.replaceAll(newAcqs[i], ogAcqs[i]);
		}
		$('.AcquirerList').append(acquirerClone);
		for(var k = 0; k < checkedAcquirer.length; k++){
			$(".AcquirerList .Acquirer" + cloneIndex + ' input[type="radio"]').each(function(){
				if($(this).val() == checkedAcquirer[k]){
					$(this).attr("disabled", "true");
				}
			});
		}
		if(cloneIndex == 2){
			$(acquirerRemoveBtn).insertAfter('.AcquirerList + .acquirerCloneBtn');
		}
		$('.AcquirerList + .acquirerCloneBtn').hide();
		$('.AcquirerList input[name="Acquirer'+cloneIndex+'"]').on('click', function(){
			if($(this).attr('name') === 'Acquirer' + cloneIndex){
				$('.AcquirerList + .acquirerCloneBtn').show();
			}
	    });
	})

	$('.AcquirerList input[type="radio"]').on('click', function(){

		var indexElem = parseInt($(this).attr('name').replace("Acquirer", "")) - 1;
				if(checkedAcquirer[indexElem] !== undefined){
					checkedAcquirer[indexElem] = $(this).val();


					while(indexElem < cloneIndex){
						$(".AcquirerList .Acquirer" + (indexElem + 1) + ' input[type="radio"]').removeAttr("disabled");
				for(var k = 0; k < indexElem; k++){
					$(".AcquirerList .Acquirer" + (indexElem + 1) + ' input[type="radio"]').each(function(){
						if($(this).val() == checkedAcquirer[k]){
							$(this).removeAttr("checked");
							$(this).attr("disabled", "true");
						}
					});
				}
				indexElem++
			}

		}
    });




	$('.AcquirerList ~ .acquirerRemoveBtn').on('click', function(){
		checkedAcquirer.pop();
		if(cloneIndex == 2){
			$(this).remove();
		}
		if(cloneIndex > 1){
			$(".AcquirerList .Acquirer" + cloneIndex).remove();
			$('.AcquirerList + .acquirerCloneBtn').show();
			cloneIndex--;
		}
	});

	$('.AcquirerList input[name="Acquirer1"]').on('click', function(){
		$('.AcquirerList + .acquirerCloneBtn').show();
    });

	$('#offus_reset').on('click', function(){
		$('.offus_table').removeClass('disabled');
		$('.offusFormTable input').removeAttr('checked');
		$('#offus_submit').addClass('disabled');
		$('#offus_reset').addClass('disabled');
	});

	$('#onus_reset').on('click', function(){
		$('.onus_table').removeClass('disabled');
		$('.onusFormTable input').removeAttr('checked');
		$('#onus_submit').addClass('disabled');
		$('#onus_reset').addClass('disabled');
	});

    $('#onus_section input').click(function(){

    	if($('#onus_section input:checked').length > 0){
    		$('.onus_table').addClass('disabled');
    		$('#onus_reset').removeClass('disabled');
    		if( ($('#onus_section input[name="currency"]').is(':checked')) && ($('#onus_section input[name="txnType"]').is(':checked')) && ($('#onus_section input[name="paymentType"]').is(':checked')) && ($('#onus_section input[name="mopType"]').is(':checked')) && ($('#onus_section input[name="acquirer"]').is(':checked')) ){
    			$('#onus_submit').removeClass('disabled');
    		}else{
    			$('#onus_submit').addClass('disabled');
    		}
    	}else{
    		$('.onus_table').removeClass('disabled');
    		$('#onus_reset').addClass('disabled');
    	}
    });

   //Offus Edit Funtion Start Here
    var TempCloneIndex = 0;

	$('.product-spec').on('click', '.edit-row', function(events){
		var acquirerCopy=  localStorage.clickcountraw;




	    var index = $(this).attr('data-id');
	    var tempAcquirerMap;
	    var tempData;
	    $('.offusFormTable').hide();

	    for(var i = 0; i < OffusList.length; i++){
        	 if(OffusList[i].id == index){
        		 tempData = OffusList[i];
        		 tempAcquirerMap = tempData.acquirerMap.split(", ");
        		 $('<button type="button" class="btn btn-primary acquirerCloneBtn">Add</button>').insertAfter(".AcquirerListTemp_" + index);
        		 if(tempAcquirerMap.length > 2){
       			    $(acquirerRemoveBtn).insertAfter('.AcquirerListTemp_'+index+' + .acquirerCloneBtn');
           		 }
        		 for(var j = 0; j < tempAcquirerMap.length; j++){
					debugger;
        			TempCloneIndex++;
					var acquirers = $($(acquirerCopy).find('label'));
					var ogAcqs = [];
					for (var k = 1; k < acquirers.length; k++) {
						ogAcqs.push($(acquirers[k]).text());
					}
					var newAcqs = [];
					for (var k = 0; k < ogAcqs.length; k++) {
						newAcqs.push(ogAcqs[k].replace(/1/g, TempCloneIndex));
					}
					acquirerClone = acquirerCopy.replace(/1/g, TempCloneIndex);
					for (var k = 0; k < newAcqs.length; k++) {
						acquirerClone = acquirerClone.replaceAll(newAcqs[k], ogAcqs[k]);
					}
 					$('.AcquirerListTemp_' + index).append(acquirerClone);
					$(".AcquirerListTemp_" + index + " .Acquirer" + TempCloneIndex + ' input[type="radio"]').each(function(){
						if($(this).val() == tempAcquirerMap[j]){
							$(this).attr("checked", "true");
						}
	 					for(var k = 0; k < j; k++){
 							if($(this).val() == tempAcquirerMap[k]){
 								$(this).attr("disabled", "true");
 							}
	 					}
					});
 					$('.AcquirerListTemp_'+index+' input[name="Acquirer'+TempCloneIndex+'"]').on('click', function(){
 						if($(this).attr('name') === '.AcquirerListTemp_' + index + ' Acquirer' + TempCloneIndex){
 							$('.AcquirerListTemp_'+index+' + .acquirerCloneBtn').show();
 						}
 				    });

        		 }
        	 }
         }


		$('.AcquirerListTemp_'+index+' input[type="radio"]').on('click', function(){


			var indexElem = parseInt($(this).attr('name').replace("Acquirer", "")) - 1;
				if(tempAcquirerMap[indexElem] !== undefined){
					tempAcquirerMap[indexElem] = $(this).val();
					while(indexElem < TempCloneIndex){
						$(".AcquirerListTemp_"+index+" .Acquirer" + (indexElem + 1) + ' input[type="radio"]').removeAttr("disabled");
					for(var k = 0; k < indexElem; k++){
						$(".AcquirerListTemp_"+index+" .Acquirer" + (indexElem + 1) + ' input[type="radio"]').each(function(){
							if($(this).val() == tempAcquirerMap[k]){
								$(this).removeAttr("checked");
								$(this).attr("disabled", "true");
							}
						});
					}
					indexElem++
				}

			}
	    });

		$('.AcquirerListTemp_'+index+' + ~ .acquirerRemoveBtn').on('click', function(){
			tempAcquirerMap.pop();
			if(TempCloneIndex == 2){
				$(this).remove();
			}
			if(TempCloneIndex > 1){
				$(".AcquirerListTemp_"+index+" .Acquirer" + TempCloneIndex).remove();
				$('.AcquirerListTemp_'+index+'  + .acquirerCloneBtn').show();
				TempCloneIndex--;
			}
		})

		$('.AcquirerListTemp_' + index).parent().append('<button type="button" class="btn btn-primary acquirerCloneBtn" style="display:none;">Add</button>');

		$('.AcquirerListTemp_'+index+' + .acquirerCloneBtn').on('click', function(){
			debugger;
			tempAcquirerMap.push($('.AcquirerListTemp_'+index+' input[name="Acquirer'+TempCloneIndex+'"]:checked').val());
			TempCloneIndex++;
			var acquirers = $($(acquirerCopy).find('label'));
			var ogAcqs = [];
			for (var i = 1; i < acquirers.length; i++) {
				ogAcqs.push($(acquirers[i]).text());
			}
			var newAcqs = [];
			for (var i = 0; i < ogAcqs.length; i++) {
				newAcqs.push(ogAcqs[i].replace(/1/g, TempCloneIndex));
			}
			acquirerClone = acquirerCopy.replace(/1/g, TempCloneIndex);
			for (var i = 0; i < newAcqs.length; i++) {
				acquirerClone = acquirerClone.replaceAll(newAcqs[i], ogAcqs[i]);
			}

			$('.AcquirerListTemp_'+index).append(acquirerClone);
			for(var k = 0; k < tempAcquirerMap.length; k++){
				$(".AcquirerListTemp_"+index+" .Acquirer" + TempCloneIndex + ' input[type="radio"]').each(function(){
					if($(this).val() == tempAcquirerMap[k]){
						$(this).attr("disabled", "true");
					}
				});
			}
			if(TempCloneIndex == 2){
				$(acquirerRemoveBtn).insertAfter('.AcquirerListTemp_'+index+' + .acquirerCloneBtn');
			}
			$('.AcquirerListTemp_'+index+' + .acquirerCloneBtn').hide();
			$('.AcquirerListTemp_'+index+' input[name="Acquirer'+TempCloneIndex+'"]').on('click', function(){
				if($(this).attr('name') === 'Acquirer' + TempCloneIndex){
					$('.AcquirerListTemp_'+index+' + .acquirerCloneBtn').show();
				}
		    });
		});

		$(this).attr('class', 'btn btn-primary update-row').html('<i class="fa fa-check"></i> Update');
		$(this).next('.remove-row').attr('class', 'btn btn-warning cancel-row').html('<i class="fa fa-times"></i> Cancel');

		$('.edit-row').hide();


		$('.product-spec').on('click', '.cancel-row', function(events){
			window.location.reload();
		});

	});
});


function updateRuleEngine(){
	var finalTempData;
	var tempValues = [];
	var token  = document.getElementsByName("token")[0].value;
	var acquirerList = document.getElementById("selectedAcquirerList").value;
	// console.log($(tempIdData).val());
	// console.log("Acquirer :"+acquirerList);
	// console.log("Token :"+token);
	if(acquirerList == ""){
		alert("Please Select Acquirer");
		return false;
	}

	for(var i=0; i<OffusList.length;i++){
		console.log("ID :"+OffusList[i].id);
		if(OffusList[i].id == $(tempIdData).val()){
			finalTempData = OffusList[i];
		}

	}
	// console.log("finalTempData :"+JSON.stringify(finalTempData));

	finalTempData.acquirerMap = acquirerList;
	tempValues.push(finalTempData);
	var data1= "";
	data1 = data1.concat("{" , "\"", "listData","\"",":", JSON.stringify(tempValues),",\"","token","\":\"",token,"\"","}" );
	// console.log("Final :"+data1);

	$.ajax({
		type : "POST",
		url : "editRouterRule",
		timeout : 0,
		dataType : "json",
		contentType: "application/json; charset=utf-8",
		data : data1,
		success : function(data) {
			var response = data.response;
			swal({
			  title: 'Rule Updated!',
			  type: "success"
			}, function () {
				  window.location.reload();
			});
		},
		error : function(data) {
			window.location.reload();
		}
	});
}
 function sel(value){
    var typ =2;
         document.getElementById("dvpy").style.display ="block";

   sid = value;
   document.getElementById("dvmop").style.display ="none";

    var merchantVal = document.getElementById("onus_merchant").value;

    $.ajax({
		type : "GET",
		url : "getAcquirer",
		timeout : 0,
		data : {
			"type":typ,
			"payId":merchantVal,
			"Acquirer1": sid,


				"struts.token.name": "token",
			},

		success : function(data) {
			var i;
			document.getElementById("loading").style.display = "none";
			//document.getElementById("onUs_default").classList.remove('active');
			OnusList = data.acquirerList1;

			var acqHtml ="";
			for(var itm =0; itm< data.acquirerList1.length;itm++)
			{

			  acqHtml = acqHtml+"<div class='checkbox'><label><div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='wwctrl' ><input type='radio'    name='paymentType' value="+data.acquirerList1[itm]+" onclick='pay(this.value)' id='acquirer1'><input type='hidden' id='__checkbox_acquirer' name='paymentType' value='"+data.acquirerList1[itm]+"'>"+data.acquirerList1[itm]+"</div> </div></label></div><br>"

			}
			document.getElementById("dvpy").innerHTML=acqHtml;



	}
	    });

}

var tempIdData;

function editMethod(cont){

	var modal = document.getElementById("myModal");

	var span = document.getElementsByClassName("close")[0];
	modal.style.display = "block";

	console.log("Edit Method Clicked");
	tempIdData = $($(cont).closest('tr').find('td')[0]).html();
	var merchant = $($(cont).closest('tr').find('td')[1]).html();
	var currency = $($(cont).closest('tr').find('td')[2]).html();
	var paymentType = $($(cont).closest('tr').find('td')[3]).html();
	var mopType = $($(cont).closest('tr').find('td')[4]).html();
	console.log("Merchant :"+merchant+" Currency :"+currency+" PaymentType :"+paymentType+" MopType :"+mopType);

	var urls = new URL(window.location.href);
	var domain = urls.origin;

	$.ajax({
		type:"GET",
		url:domain+"/crmws/ruleEngin/getAcquirerForSmartRouter?merchantName="+merchant+"&currency="+currency+"&paymentType="+paymentType+"&mopType="+mopType,
		contentType: "application/json",
		success : function(data){
			console.log("Response :"+data);
			var html = "";
			for(var i=0;i<data.length;i++){
				html+='<option value="'+data[i]+'">'+data[i]+'</option>';

			}
			$("#acquirerFinal").html(html);

		}

	});


}


function pay(value){

      var paymentt =value;
    var typ =3;
   var bank = sid ;// $(acquirer).val()
   document.getElementById("loading").style.display = "block";

   var merchantVal = document.getElementById("onus_merchant").value;
		document.getElementById("dvmop").style.display ="block";



    $.ajax({
		type : "GET",
		url : "getpaymenttype",
		timeout : 0,
		data : {
			"type":typ,
			"payId":merchantVal,
			"Acquirer1": bank,
		"paytype": paymentt,

				"struts.token.name": "token",
			},

		success : function(data) {
			var i;

			document.getElementById("loading").style.display = "none";
			OnusList = data.acquirerList2;
			var acqHtml ="";
			for(var itm =0; itm< data.acquirerList2.length;itm++)
			{
	  			 acqHtml = acqHtml+"<div class='checkbox' ><label><div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='wwctrl'><input type='checkbox'    name='mopType' value='"+data.acquirerList2[itm]+"' id='acquirer"+data.acquirerList2[itm]+"'><input type='hidden' id='acquirer"+data.acquirerList2[itm]+"' name='mopType' value='"+data.acquirerList2[itm]+"'>"+data.acquirerList2[itm]+"</label></div> </div></div><br>"
			}
			document.getElementById("dvmop").innerHTML=acqHtml;



	}
	    });

}
	function getpaymenttype1(value){
  		document.getElementById("mopid1").style.display = "none";
  		    // document.getElementById("acqdis").style.display ="block";

  		document.getElementById("paytype1").style.display = "block";
  	   var typ =3;
       var merchantVal = document.getElementById("offus_merchant").value;
       			document.getElementById("loading").style.display = "block";
		var acquirer = $('input[name="Acquirer1"]:checked').val();

  		//("sid1"+sid1);
      $.ajax({
  		type : "GET",
  		url : "getAcquirer",
  		timeout : 0,
  		data : {
  			"type":typ,
  			"payId":merchantVal,
  			"Acquirer1": acquirer,
			"selectedCurrency": value,


  				"struts.token.name": "token",
  			},

  		success : function(data) {

  			document.getElementById("loading").style.display = "none";
  			OnusList = data.acquirerList1;
  				var acqHtml ="";
  			for(var itm =0; itm< data.acquirerList1.length;itm++)
  			{
  			  acqHtml = acqHtml+"<div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='checkbox'><input type='radio'    name='paymentType' value="+data.acquirerList1[itm]+" onclick='mop1(this.value)' id='acquirer1'><label>"+data.acquirerList1[itm]+"</label></div> </div>"

  			}
  			document.getElementById("paytype1").innerHTML=acqHtml;

  	}
  	    });

  }
  function mop1(value){
  	document.getElementById("mopid1").style.display = "block";
  				document.getElementById("loading").style.display = "block";

  	   var paymentt =value;
         var typ =4;
      var merchantVal = document.getElementById("offus_merchant").value;
	  var selectedCurrency = $('input[name="selectedCurrency"]:checked').val();
	  var acquirer = $('input[name="Acquirer1"]:checked').val();
  	   $.ajax({
  		type : "GET",
  		url : "getpaymenttype",
  		timeout : 0,
  		data : {
  			"type":typ,
  			"payId":merchantVal,
  			"Acquirer1": acquirer,
  			"paytype": paymentt,
			"selectedCurrency" : selectedCurrency,

  				"struts.token.name": "token",
  			},

  		success : function(data) {
  			document.getElementById("loading").style.display = "none";
  				OnusList = data.acquirerList2;
  			var acqHtml ="";
  				for(var itm =0; itm< data.acquirerList2.length;itm++)
  			{

  			 acqHtml = acqHtml+"<div class='checkbox' ><label><div id='wwgrp_acquirer' class='wwgrp'><div id='wwctrl_acquirer' class='wwctrl'><input type='checkbox'    name='mopType' value='"+data.acquirerList2[itm]+"' id='acquirer1"+data.acquirerList2[itm]+"'><input type='hidden' id='acquirer1"+data.acquirerList2[itm]+"' name='mopType' value='"+data.acquirerList2[itm]+"'>"+data.acquirerList2[itm]+"</label></div> </div></div><br>"
  			}
  			document.getElementById("mopid1").innerHTML=acqHtml;
  		}
  	    });

  }





  function myFun(){
		var favorite = [];
	    $.each($("input[name='sport']:checked"), function(){
	        favorite.push($(this).val());
	    });
	    if (0 < favorite.length)    {

		var merchantVal = document.getElementById("selectMerchant").value;

	var abhi=favorite;



			var token  = document.getElementsByName("token")[0].value;
			swal({
				title: "Are you sure want to delete this Rule?",
				type: "warning",
				showCancelButton: true,
				confirmButtonColor: "#DD6B55",
				confirmButtonText: "Yes, delete it!",
				closeOnConfirm: false
				}, function (isConfirm) {
					if (!isConfirm) return;
		    $.ajax({
				type : "POST",
				url : "deletelist",
				timeout : 0,
				 dataType : "json",
			        traditional: true,
				data : {
					"payid":merchantVal,
					"dataid":favorite,
					"abhi":abhi,
					"token":token,
					"struts.token.name": "token",

				},
				success : function(data) {
					var response = data.response;
					swal({
					 title: 'Deleted Successful!',
					 type: "success"
					}, function(){
						$(this).closest('tr').remove();
						window.location.reload();
					});
				},
				error : function(data) {
					window.location.reload();
				}
		    });
		});
  }else{
	  alert("Please select a rule");
  }

		};

		function mychecktest(){
			  document.getElementById("myCheck").checked = false;

		};

		function toggle(source) {
		  checkboxes = document.getElementsByName('sport');
		  for(var i=0, n=checkboxes.length;i<n;i++) {
		    checkboxes[i].checked = source.checked;
		  }
		}
function enbaleButtonAsPerAccess() {
	if ($('#ruleEngine').hasClass("active")) {
			var access = document.getElementById("menuAccessForRule").value;
			if (access.includes("Update")) {
				var editButtons = document.getElementsByName("editRule");
				for (var i = 0; i < editButtons.length; i++) {
					var edit = editButtons[i];
					edit.disabled=false;
				}
			}
			if (access.includes("Delete")) {
				var deleteButtons = document.getElementsByName("deleteRule");
				for (var i = 0; i < deleteButtons.length; i++) {
					var deleteBtn = deleteButtons[i];
					deleteBtn.disabled=false;
				}
			}
	}
}

function enbaleButtonAsPerAccess() {
	if ($('#ruleEngine').hasClass("active")) {
			var access = document.getElementById("menuAccessForRule").value;
			if (access.includes("Update")) {
				var editButtons = document.getElementsByName("editRule");
				for (var i = 0; i < editButtons.length; i++) {
					var edit = editButtons[i];
					edit.disabled=false;
				}
			}
			if (access.includes("Delete")) {
				var deleteButtons = document.getElementsByName("deleteRule");
				for (var i = 0; i < deleteButtons.length; i++) {
					var deleteBtn = deleteButtons[i];
					deleteBtn.disabled=false;
				}
			}
	}
}




function getCurrency(acquirer){




	document.getElementById("mopid1").style.display = "none";
	document.getElementById("loading").style.display = "block";
	document.getElementById("currencyList").style.display = "none";

	var typ =2;
	var merchantVal = document.getElementById("offus_merchant").value;
		if(merchantVal == "" ||  merchantVal == null){
			alert("Please Select Merchant");
			document.getElementById("loading").style.display = "none";
			return false;
		}


	$.ajax({

	type : "GET",
	url : "getdata",
	timeout : 0,
	data : {

		"type":typ,
			"payId":merchantVal,
			"Acquirer1":acquirer,
			"struts.token.name": "token",
		},

	success : function(d) {

		var data = d.currencyMap;
		console.log(data);
		document.getElementById("loading").style.display = "none";
		var currencyHtml ="";
		for(let key in data){
			currencyHtml = currencyHtml+"<div class='wwgrp'><div class='checkbox' width='100%'><input type='radio'   onclick='getpaymenttype1(this.value)' name='selectedCurrency' id='currency"+data[key]+"' value='"+key+"' ><label for='currency"+data[key]+"'>"+data[key]+"</label></div></div>"
		}
		document.getElementById("currencyList").innerHTML=currencyHtml;
		document.getElementById("currencyList").style.display = "block";
	},
	error : function(data) {
		window.location.reload();
	}
	});
}