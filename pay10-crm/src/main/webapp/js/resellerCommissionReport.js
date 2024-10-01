$(document)
		.ready(
				function() {
					
					$(function() {
						  var fromDate = new Date();
						  fromDate.setMonth(fromDate.getMonth() -2);
						  fromDate.setDate(fromDate.getDate() -2);
						$("#dateFrom").flatpickr({
							minDate : fromDate,
							maxDate: new Date(),
							defaultDate: "today"
							
						});
						$("#dateTo").flatpickr({
							minDate : fromDate,
							maxDate: new Date(),
							defaultDate: "today"
						});
					});

					
			
						getDetails();
						
				
				});

function getDetails() {
	
	var date1 = document.getElementById("dateFrom").value;
	var date2 = document.getElementById("dateTo").value;
	var reseller = document.getElementById("resellerId").value;
	var merchantId = document.getElementById("merchantDropdown").value;
	var paymentType = document.getElementById("paymentDropdown").value;
	var urls = new URL(window.location.href);
	var domain = urls.origin;

		if (date1 > date2) {
			alert('From date must be before the to date');
			$('#loader-wrapper').hide();
			$('#dateFrom').focus();
			return false;
		}

		$.ajax({

			type : "GET",
			url : domain+"/crmws/reseller/getResellerCommissionReport",

			data : {
				
				"dateFrom" : date1,
				"dateTo" : date2,
				"resellerId" : reseller,
				"merchantId" : merchantId,
				"paymentType" : paymentType,
				
			},
			success : function(response) {
					if (response!=null && response!= ''){
					var res=JSON.parse(response);
				
					$("#CCTotalCount").text(res.totalCCTxn);
					$("#DCTotalCount").text(res.totalDCTxn);
					$("#UPTotalCount").text(res.totalUPTxn);
					$("#WLTotalCount").text(res.totalWLTxn);
					$("#NBTotalCount").text(res.totalNBTxn);
					$("#TotalTransactionCount").text(res.totalTxnCount);
					
					$("#ccTotalAmount").text(res.totalCCCom);
					$("#dcTotalAmount").text(res.totalDCCom);
					$("#upTotalAmount").text(res.totalUPCom);
					$("#wlTotalAmount").text(res.totalWLCom);
					$("#nbTotalAmount").text(res.totalNBCom);
					$("#CCTxnAmount").text(res.CCTxnAmount);
					$("#DCTxnAmount").text(res.DCTxnAmount);
					$("#UPTxnAmount").text(res.UPTxnAmount);
					$("#WLTxnAmount").text(res.WLTxnAmount);
					$("#NBTxnAmount").text(res.NBTxnAmount);
					
					$("#TotalTxnAmount").text(res.TotalTxnAmount);
					$("#TotalCommAmount").text(res.totalCom);
					
					
				  if(paymentType=='CREDIT_CARD'){
					
					$('#dc').hide();
					$('#nb').hide();
					$('#up').hide();
					$('#wl').hide();
					$('#cc').show();
					}
					else if(paymentType=='DEBIT_CARD'){	
						$('#cc').hide();
						$('#up').hide();
						$('#nb').hide();
						$('#wl').hide();
						$('#dc').show();
					
					}
					else if(paymentType=='UPI'){	
						$('#cc').hide();
						$('#dc').hide();
						$('#nb').hide();
						$('#wl').hide();
						$('#up').show();
					
					}
	               else if(paymentType=='NET_BANKING'){	
	            	    $('#cc').hide();
						$('#dc').hide();
						$('#up').hide();
						$('#wl').hide();
						$('#nb').show();	
					}
	               else if(paymentType=='WALLET') {	
	            	    $('#cc').hide();
						$('#dc').hide();
						$('#nb').hide();
						$('#up').hide();
						$('#wl').show();
					}
	               else{
	            		$('#cc').show(); 
	            		$('#dc').show();
	            		$('#nb').show();
	            		$('#wl').show();
	            		$('#up').show();
	               }
				}
			},
		});
	}


function getMerchant(resellerId) {
	
	$.ajax({

		type : "GET",
		url : "getResellerMerchant",
		timeout : 0,
		data : {

			"resellerId" : resellerId,

		},
		success : function(data, status) {
			// var response= JSON.stringify(data);
			var s = '<option value="">select merchant</option>';
			// alert(s)
			for (var i = 0; i < data.merchantarreList.length; i++) {
				s += '<option value="' + data.merchantarreList[i] + '">'
						+ data.merchantarrename[i] + '</option>';
				// console.log(s)
				// alert(s)
			}
			document.getElementById("tomerchant").style.display = "block";

			$("#merchantDropdown").html(s);

		}
	});
}


function getPaymentType(payId) {

	$.ajax({
		type : "GET",
		url : "getMerchantPaymentType",
		data : {
			"merchantname" : payId,
		},
		success : function(data, status) {
			var paymentType = data.paymentTypeList;
			var s = '<option value="">Select Payment Type</option>';
			for (var i = 0; i < paymentType.length; i++) {
				s += '<option value="' + paymentType[i] + '">' + paymentType[i]
						+ '</option>';
			}
			document.getElementById("topayment").style.display = "block";

			$("#paymentDropdown").html(s);
		}
	});
}

