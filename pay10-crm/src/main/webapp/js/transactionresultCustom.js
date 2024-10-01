$(document).ready(function(){
 	  

	   
  // Initialize select2
  $(".adminMerchants").select2();
});



	$(document).ready(function() {

		$(function() {
			$("#dateFrom").datepicker({
				changeMonth: true,
			    changeYear: true,
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				minDate: new Date(2018, 1 - 1, 1),
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				changeMonth: true,
			    changeYear: true,
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				minDate: new Date(2018, 1 - 1, 1),
				maxDate : new Date()
			});
		});

		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			renderTable();
			
		});

		$("#submit").click(function(env) {
			//  Restrict for empty date field
			var date1 = document.getElementById("dateFrom").value;
			if(date1 == "" || date1== null){
				 alert("Please select Date");
				 return;
			}
			var date2 = document.getElementById("dateTo").value;
			if(date2 == "" || date2== null){
				 alert("Please select Date");
				 return;
			}
			
			$('#loader-wrapper').show();
			reloadTable();
			
		});

		$(function(){
			var datepick = $.datepicker;
			var table = $('#txnResultDataTable').DataTable();
			$('#txnResultDataTable').on('click', 'td.my_class1', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();
				
				popup(rowData.transactionIdString,rowData.oId,rowData.orderId,rowData.txnType,rowData.pgRefNum);
			});
		});
	});

	function renderTable() {
		var monthVal = parseInt(new Date().getMonth())+1;
		  var merchantEmailId = document.getElementById("merchant").value;
		var table = new $.fn.dataTable.Api('#txnResultDataTable');
		
		var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		if (transFrom > transTo) {
			$('#loader-wrapper').hide();
			alert('From date must be before the to date');
			$('#dateFrom').focus();
			return false;
		}
		if (transTo - transFrom > 31 * 86400000) {
			$('#loader-wrapper').hide();
			alert('No. of days can not be more than 31');
			$('#dateFrom').focus();
			return false;
		}
		var token = document.getElementsByName("token")[0].value;
        //$('#loader-wrapper').hide();
		
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
	
		$('#txnResultDataTable').dataTable(
						{
							"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(10).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(10, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(10).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
										
																		// Total over all pages
								total = api.column(11).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(11, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
										
										
							},
							"columnDefs": [{ 
								className: "dt-body-right",
								"targets": [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
							}],
								dom : 'BTrftlpi',
								buttons : [
										$.extend( true, {}, buttonCommon, {
											extend: 'copyHtml5',											
											exportOptions : {											
												columns : [':visible']
											},
										} ),
									$.extend( true, {}, buttonCommon, {
											extend: 'csvHtml5',
											title : 'SearchPayment_Transactions_'+(new Date().getFullYear())+(monthVal>9?monthVal:'0'+monthVal)+(new Date().getDate()>9?new Date().getDate():'0'+new Date().getDate())+(new Date().getHours()>9?new Date().getHours():'0'+new Date().getHours())+(new Date().getMinutes()>9?new Date().getMinutes():'0'+new Date().getMinutes())+(new Date().getSeconds() >9?new Date().getSeconds():'0'+new Date().getSeconds()),
											exportOptions : {
												
												columns : [':visible']
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Search Transactions',
										exportOptions : {
											columns: [':visible']
										},
										customize: function (doc) {
										    doc.defaultStyle.alignment = 'center';
					     					doc.styles.tableHeader.alignment = 'center';
										  }
									},
									// Disabled print button.
									/*{extend : 'print',//footer : true,title : 'Search Transactions',exportOptions : {columns : [':visible']}},*/
									{
										extend : 'colvis',
										columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
									} ],

							"ajax" :{
								
								"url" : "transactionSearchActionCustom",
								"type" : "POST",
								"timeout": 0,
								"data": function (d){
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
									 $("#submit").removeAttr("disabled");
									 $('#loader-wrapper').hide();
							},
							 "searching": false,
							 "ordering": false,
							 "processing": true,
						        "serverSide": true,
						        "paginationType": "full_numbers", 
						        "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
								"order" : [ [ 2, "desc" ] ],
						       
						        "columnDefs": [
						            {
						            "type": "html-num-fmt", 
						            "targets": 4,
						            "orderable": true, 
						            "targets": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17]
						            }
						        ],
 
							"columns" : [ {
								"data" : "transactionIdString",
								"className" : "txnId my_class1 text-class",
								"width": "2%" 
							},  {
								"data" : "pgRefNum",
								"className" : "payId text-class"
								
							},{
								"data" : "merchants",
								"className" : "text-class"
							}, {
								"data" : "dateFrom",
								"className" : "text-class"
							}, {
								"data" : "orderId",
								"className" : "orderId text-class"
							}, {
								"data" : "refundOrderId",
								"className" : "orderId text-class"
							}, {
								"data" : "mopType",
								"className" : "mopType text-class"
							}, {
								"data" : "paymentMethods",
								"render" : function(data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
											+ ' ' + full['mopType'];
								},
								"className" : "text-class"
							}, {
								"data" : "txnType",
								"className" : "txnType text-class",
							}, {
								"data" : "status",
								"className" : "status text-class"
							}, {
								"data" : "amount",
								"className" : "text-class",
								"render" : function(data){
									return inrFormat(data);
								}
							}
							, {
								"data" : "totalAmount",
								"className" : "text-class",
								"visible" : false,
								"render" : function(data){
									return inrFormat(data);
								}
							},{
								"data" : "payId",
								"visible" : false
								
							}, {
								"data" : "customerEmail",
								"className" : "text-class"
							}, {
								"data" : "customerPhone",
								"className" : "text-class"
							},{
								"data" : "acquirerType",
								"className" : "acquirerType text-class"
							},
								
							
							{
								"data" : "customerPhone",
								"visible" : false
							},  {
								"data" : null,
								"visible" : false,
								"className" : "displayNone",
								"mRender" : function(row) {
									return "\u0027" + row.transactionIdString;
								}
							}, {
								"data" : "customerPhone",
								"visible" : false,
								"className" : "displayNone"
							}, {
								"data" : "customerPhone",
								"visible" : false,
								"className" : "displayNone"
							},
							 {
								"data" : "oId",
								"visible" : false,
								"className" : "displayNone"
							}]
						});
						
		$(document).ready(function() {

					var table = $('#txnResultDataTable').DataTable();
				$('#txnResultDataTable').on('click','.center',function(){
					var columnIndex = table.cell(this).index().column;
					var rowIndex = table.cell(this).index().row;
					var rowNodes = table.row(rowIndex).node();
					var rowData = table.row(rowIndex).data();
					var txnType1 = rowData.txnType;
					var status1 = rowData.status;	
				
					if ((txnType1=="SALE" && status1=="Captured")||(txnType1=="AUTHORISE" && status1=="Approved")||(txnType1=="SALE" && status1=="Settled")) {						
						var payId1 =  rowData.pgRefNum;										
						var orderId1 = rowData.orderId; 					 
						var txnId1 = Number(rowData.transactionIdString); 
						document.getElementById('payIdc').value = payId1;
						document.getElementById('orderIdc').value = orderId1;
						document.getElementById('txnIdc').value = txnId1;
					    document.chargeback.submit();
					}
			});
		});
		
			
		
	}

	function reloadTable() {
		var datepick = $.datepicker;
		var transFrom = $.datepicker
				.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		if (transFrom > transTo) {
			alert('From date must be before the to date');
			$('#loader-wrapper').hide();
			$('#dateFrom').focus();
			return false;
		}
		if (transTo - transFrom > 31 * 86400000) {
			alert('No. of days can not be more than 31');
			$('#loader-wrapper').hide();
			$('#dateFrom').focus();
			return false;
		}
		$("#submit").attr("disabled", true);
		var tableObj = $('#txnResultDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}


	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var merchantEmailId = document.getElementById("merchant").value;
		var	transactionType = document.getElementById("transactionType").value;
		var paymentType = document.getElementById("selectBox3").title;
		var status = document.getElementById("selectBox4").title;
		var currency = document.getElementById("currency").value;
		var mopType = document.getElementById("selectBox2").title;	
		var acquirer = document.getElementById("selectBox1").title;
		
		if(merchantEmailId==''){
			merchantEmailId='ALL'
		}
		if(transactionType==''){
			transactionType='ALL'
		}
		if(paymentType==''){
			paymentType='ALL'
		}
		if(status==''){
			status='ALL'
		}
		if(currency==''){
			currency='ALL'
		}		
		if(mopType==''){
			mopType='ALL'
		}
		if(acquirer==''){
			acquirer='ALL'
		}
		
		var obj = {
			transactionId : document.getElementById("pgRefNum").value,
			orderId : document.getElementById("orderId").value,
			customerEmail : document.getElementById("customerEmail").value,
			customerPhone : document.getElementById("custPhone").value,
			mopType:mopType,
			acquirer:acquirer,
			merchantEmailId : merchantEmailId,
			transactionType : transactionType,
			paymentType : paymentType,
			status : status,
			currency : currency,
			dateFrom : document.getElementById("dateFrom").value,
			dateTo : document.getElementById("dateTo").value,
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}
	
	


	function popup(txnId , oId , orderId , txnType , pgRefNum) {
		
		var token = document.getElementsByName("token")[0].value;
		
		var myData = {
				token : token,
				"struts.token.name" : "token",
				"transactionId":txnId,
				"oId":oId,
				"orderId":orderId,
				"txnType":txnType,
				"pgRefNum":pgRefNum
		}
		$.ajax({
		    	url: "customerAddressActionAdmin",
				timeout : 0,
		    	type : "POST",
		    	data :myData,
		    	success: function(response){
					var responseObj =  response.aaData;
					var transObj = response.trailData[0];
					var txt = document.createElement("textarea");
					
					/* Start Billing details  */	
					$('#sec1 td').eq(0).text(responseObj.custName ? responseObj.custName : 'Not Available');
                    $('#sec1 td').eq(1).text(responseObj.custPhone ? responseObj.custPhone : 'Not Available');
                    
                    $('#sec2 td').eq(0).text(responseObj.custCity ? responseObj.custCity : 'Not Available');
                    $('#sec2 td').eq(1).text(responseObj.custState ? responseObj.custState : 'Not Available');

					$('#sec7 td').eq(0).text(responseObj.custCountry ? responseObj.custCountry : 'Not Available');
					$('#sec7 td').eq(1).text(responseObj.custZip ? responseObj.custZip : 'Not Available');

					$('#address1 td').text(responseObj.custStreetAddress1 ? responseObj.custStreetAddress1 : 'Not Available');
					$('#address2 td').text(responseObj.custStreetAddress2 ? responseObj.custStreetAddress2 : 'Not Available');
					/* End Billing details  */
						/* Start Payment details  */
					
					$('#sec3chn td').eq(0).text(responseObj.cardHolderName ? responseObj.cardHolderName : 'Not Available');						
					
					$('#sec3 td').eq(0).text(responseObj.cardMask ? responseObj.cardMask : 'Not Available');
                    $('#sec3 td').eq(1).text(responseObj.issuer ? responseObj.issuer : 'Not Available');
                    
					$('#sec4 td').eq(0).text(responseObj.acquirerType ? responseObj.acquirerType : 'Not Available');
					$('#sec4 td').eq(1).text(responseObj.mopType ? responseObj.mopType : 'Not Available');
					
					$('#sec5 td').eq(0).text(responseObj.pgTdr ? responseObj.pgTdr : 'Not Available');
                    $('#sec5 td').eq(1).text(responseObj.pgGst ? responseObj.pgGst : 'Not Available');
                    
					$('#sec6 td').eq(0).text(responseObj.acquirerTdr ? responseObj.acquirerTdr : 'Not Available');
					$('#sec6 td').eq(1).text(responseObj.acquirerGst ? responseObj.acquirerGst : 'Not Available');

					$('#address5 td').text(responseObj.pgTxnMsg ? responseObj.pgTxnMsg : 'Not Available');
                    /* End Payment details  */
					/*Added by CS on 27 Dec for Shipping Details */
					$('#sec8 td').eq(0).text(responseObj.custShipName ? responseObj.custShipName : 'Not Available');
					$('#sec8 td').eq(1).text(responseObj.custShipPhone ? responseObj.custShipPhone : 'Not Available');

					$('#sec9 td').eq(0).text(responseObj.custShipCity ? responseObj.custShipCity : 'Not Available');
					$('#sec9 td').eq(1).text(responseObj.custShipState ? responseObj.custShipState : 'Not Available');
					
					$('#sec10 td').eq(0).text(responseObj.custShipCountry ? responseObj.custShipCountry : 'Not Available');
					$('#sec10 td').eq(1).text(responseObj.custShipZip ? responseObj.custShipZip : 'Not Available');
					

					$('#address3 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress1 : 'Not Available');
					$('#address4 td').text(responseObj.custShipStreetAddress2 ? responseObj.custShipStreetAddress2 : 'Not Available');
					/*End Shipping details */
					/*Start Transaction details */
				
					
					$( "#transactionDetails" ).empty();
					var table = document.getElementById("transactionDetails");
					var tr = document.createElement('tr');
					var th1 = document.createElement('th');
					var th2 = document.createElement('th');
					var th3 = document.createElement('th');
					var th4 = document.createElement('th');
					th1.appendChild(document.createTextNode("Order ID"));
					th2.appendChild(document.createTextNode("Transaction Type"));
					th3.appendChild(document.createTextNode("Date"));
					th4.appendChild(document.createTextNode("Status"));
					tr.appendChild(th1);
					tr.appendChild(th2);
					tr.appendChild(th3);
					tr.appendChild(th4);
					
					table.appendChild(tr);
					
					var totalRefundamount = 0;
					var totalSaleamount = 0;					
					var internalCustip;
					
					var curAmount;
					var curStatus;
					var curOrderId;
					var curPgRefNum;
					var curArn;
					var curRrn;
					
					var curTxnType;
					for(var i = 0; i < response.trailData.length; i++){
						
						var tr2 = document.createElement('tr');
						
						var td1 = document.createElement('td');
						var td2 = document.createElement('td');
						var td3 = document.createElement('td');
						var td4 = document.createElement('td');
						
						
						if(response.trailData[i].txnType == "REFUND"){
							txt.innerHTML = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 'Not Available');
						td1.appendChild(document.createTextNode(txt.value));
						}
						if(response.trailData[i].txnType == "SALE"){
							txt.innerHTML = (response.trailData[i].orderId ? response.trailData[i].orderId : 'Not Available');
						td1.appendChild(document.createTextNode(txt.value));
						}
						
						td2.appendChild(document.createTextNode(response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available'));
						td3.appendChild(document.createTextNode(response.trailData[i].createDate ? response.trailData[i].createDate : 'Not Available'));
						td4.appendChild(document.createTextNode(response.trailData[i].status ? response.trailData[i].status : 'Not Available'));
						
						tr2.appendChild(td1);
						tr2.appendChild(td2);
						tr2.appendChild(td3);
						tr2.appendChild(td4);
						
						table.appendChild(tr2);
						if(response.trailData[i].txnType == "REFUND" && response.trailData[i].status == "Captured"){							
							totalRefundamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
						}
						
						if(response.trailData[i].txnType == "SALE" && response.trailData[i].status == "Captured"){							
							totalSaleamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
						}
						
						if(response.trailData[i].transactionId == txnId){							
							curAmount = (response.trailData[i].amount ? response.trailData[i].amount : 0);
							curStatus = (response.trailData[i].currentStatus ? response.trailData[i].currentStatus : 0);
							if(response.trailData[i].txnType == "SALE"){
								curOrderId = (response.trailData[i].orderId ? response.trailData[i].orderId : 0);
							}else{
								curOrderId = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 0);
							}
							
							curPgRefNum = (response.trailData[i].pgRefNum ? response.trailData[i].pgRefNum : 0);
							curArn = (response.trailData[i].arn ? response.trailData[i].arn : 0);
							curRrn = (response.trailData[i].rrn ? response.trailData[i].rrn : 0);
							curTxnType = (response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available');
						}
						
						internalCustip = (response.trailData[i].internalCustIP ? response.trailData[i].internalCustIP : 0);
					}
					
					$('#sec14 td').eq(0).text(transObj.acqId ? transObj.acqId : 'Not Available');
					$('#sec14 td').eq(1).text(inrFormat(totalRefundamount ? totalRefundamount+".00" : 'Not Available'));

					$('#sec16 td').eq(0).text(internalCustip ? internalCustip : 'Not Available');
					$('#sec16 td').eq(1).text(inrFormat(totalSaleamount ? totalSaleamount+".00" : 'Not Available'));
					
					if(curTxnType == "SALE"){
							$('#sec16 td').eq(1).css( "display", "none" );
							$('#sec16h th').eq(1).css( "display", "none" );
					}
					if(curTxnType == "REFUND"){
							$('#sec16 td').eq(1).css( "display", "block" );
							$('#sec16h th').eq(1).css( "display", "block" );
					}

					
                    $('#sec11 td').eq(0).text(inrFormat(curAmount ? curAmount : 'Not Available'));
					$('#sec11 td').eq(1).text(curStatus ? curStatus : 'Not Available');


					txt.innerHTML = (curOrderId ? curOrderId : 'Not Available');
	
					$('#sec12 td').eq(0).text(txt.value);
					$('#sec12 td').eq(1).text(curPgRefNum ? curPgRefNum : 'Not Available');

					$('#sec13 td').eq(0).text(curArn ? curArn : 'Not Available');
					$('#sec13 td').eq(1).text(curRrn ? curRrn : 'Not Available');		


					
					
                    /*End Transaction details */
					
					
					// $('#address6 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress2 : 'Not Available');
					
					// $('#auth td').text(responseObj.internalTxnAuthentication ? responseObj.internalTxnAuthentication : 'Not Available');
					
				$('#popup').show();
		    	},
		    	error: function(xhr, textStatus, errorThrown){
			       
			    }
		});

	};


function validPgRefNum(){
	
	var pgRefValue = document.getElementById("pgRefNum").value;
	var regex = /^(?!0{16})[0-9\b]{16}$/;
	if(pgRefValue.trim() != ""){
		if(!regex.test(pgRefValue)) {
			document.getElementById("validValue").style.display= "block";
			document.getElementById("submit").disabled = true;
			document.getElementById("download").disabled = true;
        }
		else {
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
			document.getElementById("validValue").style.display= "none";
		 }
	}
	else {
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validValue").style.display= "none";
    }
}

function validateCustomerEmail(emailField){
  
	var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
  if (emailField.value !== "") {
    if (reg.test(emailField.value) == false) 
    {
		document.getElementById("validEamilValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}else{
			if(document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validEamilValue").style.display= "none";
	}
  }else{
			if(document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validEamilValue").style.display= "none";
  }

}

function validateCustomerPhone(phoneField){
	var phreg =/^([0]|\+91)?[- ]?[56789]\d{9}$/;
	// mobileRegex = /^[789]\d{9}/ --> this regex we are using the sign up page
  if (phoneField.value !== "" ) {

    if (phreg.test(phoneField.value) == false) 
    {
		document.getElementById("validPhoneValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validPhoneValue").style.display= "none";

	}
  }else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validPhoneValue").style.display= "none";
  }

}

function removeSpaces(fieldVal){
	setTimeout(function() {
	var nospacepgRefVal = fieldVal.value.replace(/ /g, "");
	fieldVal.value = nospacepgRefVal;
	}, 400);
}

function validateOrderIdvalue(orderId){
setTimeout(function() {	
	//var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
	var orderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
  if (orderId.value !== "") {
    if (orderIdreg.test(orderId.value) == false) 
    {
		document.getElementById("validOrderIdValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validPhoneValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validOrderIdValue").style.display= "none";

	}
  }else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validPhoneValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validOrderIdValue").style.display= "none";
  }
}, 400);
}

function validateOrderId(event) {
	//var regex = /^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
	var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
	  var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	  if (!regex.test(key)) {
		 event.preventDefault();
		 return false;
	  }
}


	  

	  