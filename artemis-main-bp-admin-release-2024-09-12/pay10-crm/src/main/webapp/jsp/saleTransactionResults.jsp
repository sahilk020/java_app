	<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Sale Captured</title>
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/commanValidate.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#udf4").hide();
});

</script>
<script type="text/javascript">
	function getMopType(value, id) {
    					var merchantemail = document.getElementById("merchant").value;
    					var paytype = value;

    					$.ajax({
    						type: "GET",
    						url: "GetMoptype",
    						timeout: 0,
    						data: {
    							"merchantemail": merchantemail,
    							"payment": paytype,
    							"struts.token.name": "token",
    						},
    						success: function (data) {
    							debugger
    							var mopresult = [];

    							mopresult = data.moplist;
    							/* var mopdiv ="";
    							   for(var i = 0; i < mopresult.length; i++){

    								 mopdiv=mopdiv+" <option value='"+mopresult[i].uiName+"'>"+mopresult[i].name+"</option>"


    							   } */
    							$('#' + id).html("");
    							const countriesDropDown = document.getElementById(id);
    							let option = document.createElement("option");
								option.setAttribute('value', "ALL");

								let optionText = document.createTextNode("ALL");
								option.appendChild(optionText);

								countriesDropDown.appendChild(option);

								for (let key in mopresult) {
    								let option = document.createElement("option");
    								option.setAttribute('value', data.moplist[key].code);

    								let optionText = document
    									.createTextNode(data.moplist[key].name);
    								option.appendChild(optionText);

    								countriesDropDown.appendChild(option);
    							}

    							// document.getElementById("getid").innerHTML=mopdiv;

    							// const select = document.querySelector('select');
    							// select.options.add(new Option("+mopresult[i].name+", "+mopresult[i].uiName+"))

    						}
    					});
    				}

	function handleChange() {
		debugger
	var transFrom = document.getElementById('kt_datepicker_1').value;
	var transTo =document.getElementById('kt_datepicker_2').value;
	var dateFrom=new Date(Date.parse(transFrom));
	var dateTo=new Date(Date.parse(transTo));
		if (dateFrom == null || dateTo == null) {
			alert('Enter date value');
			return false;
		}
		else if (dateFrom > dateTo) {
			alert('From date must be before the to date');
			$('#kt_datepicker_1').focus();
			$("#kt_datepicker_2").flatpickr({
	showOtherMonths: true,
				dateFormat: 'Y-m-d',
				selectOtherMonths: false,
				defaultDate: 'today',
				maxDate: new Date()
	});
			/* $("#kt_datepicker_1").flatpickr({
				showOtherMonths: true,
							dateFormat: 'Y-m-d',
							selectOtherMonths: false,
							defaultDate: 'today',
							maxDate: new Date()
				}); */
			return false;
		}
		else if (dateTo - dateFrom > 31 * 86400000) {
			alert('No. of days can not be more than 31');
			$('#kt_datepicker_1').focus();
			$("#kt_datepicker_2").flatpickr({
	showOtherMonths: true,
				dateFormat: 'Y-m-d',
				selectOtherMonths: false,
				defaultDate: 'today',
				maxDate: new Date()
	});
			$("#kt_datepicker_1").flatpickr({
				showOtherMonths: true,
							dateFormat: 'Y-m-d',
							selectOtherMonths: false,
							defaultDate: 'today',
							maxDate: new Date()
				});
			return false;
		}
		else{
			populateDataTable();
		}
 }

	/* $("#kt_datepicker_1").flatpickr({
		showOtherMonths: true,
		dateFormat: 'Y-m-d',
		selectOtherMonths: false,
		defaultDate: "today",
		maxDate: new Date()
	}); */
	$("#kt_datepicker_2").flatpickr({
		showOtherMonths: true,
		dateFormat: 'Y-m-d',
		selectOtherMonths: false,
		defaultDate: "today",
		maxDate: new Date()
	});
	$("#kt_datatable_vertical_scroll").DataTable({
		"scrollY": true,
		"scrollX": true
	});
</script>

<script type="text/javascript">
var table='';
function cancelbutton(){
	$(function () {
        $("#btnRefundCancel").click(function () {
            $("#refundAccept").modal("hide");
        });
    });
}
window.onload = function(){
//alert("body Onload");

//var token  = document.getElementsByName("token")[0].value;
var token = document.getElementsByName("token")[0].value;
		var merchantEmailId = document.getElementById("merchant").value;

		var	transactionType = "Captured";

		var	paymentType = document.getElementById("paymentType").value;

		var status = "SALE";

		//var currency = document.getElementById("currency").value;
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
		// if(currency==''){
		// 	currency='ALL'
		// }
		$("#kt_datepicker_1").flatpickr({
			maxDate: new Date(),
			dateFormat: "Y-m-d",
			defaultDate: "today"
		});
	$("#kt_datepicker_2").flatpickr({
			maxDate: new Date(),
			dateFormat: "Y-m-d",
			defaultDate: "today"
		});
$("#kt_datatable_vertical_scroll").DataTable({
	 	scrollY: true,
		scrollX: true,

	 });

var today = new Date();
var dd = today.getDate();
var mm = today.getMonth()+1;
var yyyy = today.getFullYear();
if(dd<10)
{
    dd='0'+dd;
}
if(mm<10)
{
    mm='0'+mm;
}
today = dd+'-'+mm+'-'+yyyy;


//alert("token"+token);
$("#searchAgentDataTable").DataTable({
	dom: 'Brtipl',
			 buttons: [
		            {
		                extend: 'print',
		                exportOptions: {
		                    columns: ':visible'
		                }
		            }, {
		                extend: 'csv',
		                exportOptions: {
		                    columns: ':visible'
		                }
		            },{
		                extend: 'copy',
		                exportOptions: {
		                    columns: ':visible'
		                }
		            },
		            'colvis','excel', 'pdf', 'print',
		        ],
  scrollY: true,
			scrollX: true,
            searchDelay: 500,
            processing: false,
//serverSide: true,
            order: [[5, 'desc']],
          paging: true,
	"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(9).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(9, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(9).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


								// Total over all pages
								total = api.column(8).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(9, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(8).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
							},


					"ajax" : {
						"url" : "saleTransactionSearchAction",
						"type" : "POST",
						"data" : {

							transactionId : document.getElementById("pgRefNum").value,
							orderId : document.getElementById("orderId").value,
							customerEmail : document.getElementById("customerEmail").value,
							phoneNo :document.getElementById("phoneNo").value,
							mopType :"ALL",
							merchantEmailId : merchantEmailId,
							transactionType : transactionType,
							paymentType : "ALL",
							status : status,
							currency : "ALL",
							channel: "ALL",
							dateFrom : today,
							dateTo : today,
							// draw : d.draw,
							// length :d.length,
							// start : d.start,
							draw: "2",
							length: "10000",
							start: "0",
							token : token,
							"struts.token.name" : "token",

								}
					},
					"bProcessing" : true,
					"bLengthChange" : true,
					"bDestroy" : true,
					"iDisplayLength" : 10,
					"order" : [ [ 1, "desc" ] ],
					"aoColumns" : [
						{ data: 'pgRefNum' },
						{ data: 'merchants' },
			            { data: 'dateFrom' },
		                { data: 'orderId' },
						{ data:"mopType",
							 render: function(data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
											+ ' ' + full['mopType'];
								}
							},
		                { data: 'txnType' },
		                { data: 'status' },
						{ data: 'customerEmail' },
		                { data: 'amount' },
		                { data: 'totalAmount' },
		                { data: 'currency' },
					]
				});


$(document).ready(function() {
	var totalRefund = "0.00";

	table = $('#searchAgentDataTable').DataTable();
		$('#searchAgentDataTable tbody').on('click','td',function(){
			var rows = table.rows();
			var columnVisible = table.cell(this).index().columnVisible;
			var rowIndex = table.cell(this).index().row;

		//	$('.order_Id').eq(tableIndex+2).html()
			if (columnVisible == 14) {
				var orderId = table.cell(rowIndex, 3).data();
				var pgref = table.cell(rowIndex, 0).data();
				var amount = table.cell(rowIndex, 8).data();
				var totalamount = table.cell(rowIndex, 9).data();
				var currency = table.cell(rowIndex, 10).data();

				var orderId = table.cell(rowIndex, 3).data();

		}
			var myData = {
					token : token,
					"struts.token.name" : "token",
					"orderId": orderId
				}
				$.ajax({
						url: "totalRefundByOrderIdAction",
						timeout : 0,
						type : "POST",
						data :myData,
						success: function(response){
							totalRefund	= response.totalRefund;

				/* document.getElementById("pgRefConf").value = $('.pg_Ref_Num').eq(tableIndex+2).html();
				document.getElementById("orderIDConf").value = $('.order_Id').eq(tableIndex+2).html();
				document.getElementById("refundOrderIDConf").value = '';
				var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
				var amtAvailable = inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
				document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))): inrFormat(parseFloat(totalRefund.replace(/,/g, '')))+".00";
				document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
				var amtConfi = document.getElementById("amtAvail").value ;
				document.getElementById("amtConf").value = amtConfi;
				document.getElementById("currencyConf").value = $('.currency').eq(tableIndex+2).html();
				 */

				document.getElementById("pgRefConf").value = pgref;
				document.getElementById("orderIDConf").value = orderId;
				document.getElementById("refundOrderIDConf").value = '';
				var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
				var Base2= inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
				var baseamt = Base2.toString().includes('.') ? inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
				//var baseAmt = inrFormat(parseFloat($('.amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
				var amtAvailable = inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
				document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))): inrFormat(parseFloat(totalRefund.replace(/,/g, '')))+".00";
				document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat(totalamount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(totalamount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
				var amtConfi = document.getElementById("amtAvail").value ;
				//document.getElementById("amtConf").value = baseAmt;
				document.getElementById("amtConf").value = baseamt;
				document.getElementById("currencyConf").value = currency;


				if(parseFloat("0.00") == parseFloat(amtAvailable)){
					document.getElementById("btnRefundConf").disabled = true;
				}else{
					document.getElementById("btnRefundConf").disabled = false;
				}

		        $('#refundAccept').modal('show');

						},
						error: function(xhr, textStatus, errorThrown){
						  alert("Something Went Wrong");
						}
				});
	});
});
				//document.getElementById("searchAgentDataTable").style.marginTop = "14px";

}

</script>

<script type="text/javascript">

function populateDataTable() {
	debugger
	//alert("on submit");
	var token  = document.getElementsByName("token")[0].value;
	//var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_1').val());
	//var transTo = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_2').val());

	//alert("transFrom"+transFrom);

	table=$('#searchAgentDataTable')
			.DataTable(
					{

						"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(9).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(9, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(9).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


								// Total over all pages
								total = api.column(8).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(9, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(8).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
							},
						dom: 'Brtipl',
						 buttons: [
					            {
					                extend: 'print',
					                exportOptions: {
					                    columns: ':visible'
					                }
					            },
					            'colvis','copy', 'csv', 'excel', 'pdf', 'print',
					        ],
  scrollY: true,
			scrollX: true,
            searchDelay: 500,
            processing: false,
            // serverSide: true,
            order: [[1, 'desc']],
           // stateSave: true,
            dom: 'Brtipl',
            paging: true,
						"ajax" : {
							"url" : "saleTransactionSearchAction",
							"type" : "POST",
							"data" : function(d){
								 return generatePostData(d);

									}
						},
						"bProcessing" : true,
                        "bLengthChange" : true,
                        "bDestroy" : true,
						"aoColumns" : [
							{ data: 'pgRefNum' },
			                { data: 'merchants' },
			                { data: 'dateFrom' },
			                { data: 'orderId',
			                'className' : 'order_Id'},
			                { data:"mopType",
							 render: function(data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
											+ ' ' + full['mopType'];
								}
							},
			                { data: 'txnType' },
			                { data: 'status' },
							{ data: 'customerEmail' },
			                { data: 'amount' },
			                { data: 'totalAmount' },
			                { data: 'currency' },
			            ]
					});

	/* $(document).ready(function() {
		var totalRefund = "0.00";

		table = $('#searchAgentDataTable').DataTable();
			$('#searchAgentDataTable tbody').on('click','td',function(){
				var rows = table.rows();
				var columnVisible = table.cell(this).index().columnVisible;
				var rowIndex = table.cell(this).index().row;

			//	$('.order_Id').eq(tableIndex+2).html()
				if (columnVisible == 15) {
					var orderId = table.cell(rowIndex, 3).data();
					var pgref = table.cell(rowIndex, 0).data();
					var amount = table.cell(rowIndex, 8).data();
					var totalamount = table.cell(rowIndex, 9).data();
					var currency = table.cell(rowIndex, 10).data();

					var orderId = table.cell(rowIndex, 3).data();

			}
				var myData = {
						token : token,
						"struts.token.name" : "token",
						"orderId": orderId
					}
					$.ajax({
							url: "totalRefundByOrderIdAction",
							timeout : 0,
							type : "POST",
							data :myData,
							success: function(response){
								totalRefund	= response.totalRefund;



					document.getElementById("pgRefConf").value = pgref;
					document.getElementById("orderIDConf").value = orderId;
					document.getElementById("refundOrderIDConf").value = '';
					var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
					var Base2= inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
					var baseamt = Base2.toString().includes('.') ? inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
					//var baseAmt = inrFormat(parseFloat($('.amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
					var amtAvailable = inrFormat(parseFloat(amount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
					document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))): inrFormat(parseFloat(totalRefund.replace(/,/g, '')))+".00";
					document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat(totalamount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(totalamount.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
					var amtConfi = document.getElementById("amtAvail").value ;
					//document.getElementById("amtConf").value = baseAmt;
					document.getElementById("amtConf").value = baseamt;
					document.getElementById("currencyConf").value = currency;


					if(parseFloat("0.00") == parseFloat(amtAvailable)){
						document.getElementById("btnRefundConf").disabled = true;
					}else{
						document.getElementById("btnRefundConf").disabled = false;
					}

			        $('#refundAccept').modal('show');

							},
							error: function(xhr, textStatus, errorThrown){
							  alert("Something Went Wrong");
							}
					});
		});
	}); */
				//	document.getElementById("searchAgentDataTable").style.marginTop = "14px";
}
</script>
<script type="text/javascript">

</script>
<script type="text/javascript">
	$(document).ready(function () {
		//$("#merchant").select2();
		});
$(document).ready(function(){
	document.getElementById("searchAgentDataTable_wrapper").style.marginTop = "14px";
	//document.getElementById("loadingInner").style.display = "none";
 //  document.getElementById("loading").style.display = "none";
  // Initialize select2
  $(".adminMerchants").select2();

});
</script>

<script type="text/javascript">
	$(document).ready(function() {

		// $(function() {
		// 	$("#kt_datepicker_1").datepicker({
		// 		prevText : "click for previous months",
		// 		nextText : "click for next months",
		// 		showOtherMonths : true,
		// 		dateFormat : 'dd-mm-yy',
		// 		selectOtherMonths : false,
		// 		maxDate : new Date()
		// 	});
		// 	$("#kt_datepicker_2").datepicker({
		// 		prevText : "click for previous months",
		// 		nextText : "click for next months",
		// 		showOtherMonths : true,
		// 		dateFormat : 'dd-mm-yy',
		// 		selectOtherMonths : false,
		// 		maxDate : new Date()
		// 	});
		// });

		// $(function() {
		// 	var today = new Date();
		// 	$('#kt_datepicker_2').val($.datepicker.formatDate('dd-mm-yy', today));
		// 	$('#kt_datepicker_1').val($.datepicker.formatDate('dd-mm-yy', today));
		// 	renderTable();
		// 	enableBaseOnAccess();
		// });

		$("#submit").click(function(env) {
			$('#loader-wrapper').show();
			reloadTable();
		});

		$(function(){
			// var datepick = $.datepicker;
			var table = $('#txnResultDataTable').DataTable();
			$('#txnResultDataTable').on('click', 'td.my_class', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();

				popup(rowData.oId);
			});
		});
	 });

	// function renderTable() {
	// 	var monthVal = parseInt(new Date().getMonth())+1;
	// 	  var merchantEmailId = document.getElementById("merchant").value;
	// 	var table = new $.fn.dataTable.Api('#txnResultDataTable');

	// 	var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_1').val());
	// 	var transTo = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_2').val());
	// 	if (transFrom == null || transTo == null) {
	// 		alert('Enter date value');
	// 		return false;
	// 	}

	// 	if (transFrom > transTo) {
	// 		alert('From date must be before the to date');
	// 		$('#loading').hide();
	// 		$('#kt_datepicker_1').focus();
	// 		return false;
	// 	}
	// 	if (transTo - transFrom > 31 * 86400000) {
	// 		alert('No. of days can not be more than 31');
	// 		$('#loading').hide();
	// 		$('#kt_datepicker_1').focus();
	// 		return false;
	// 	}
	// var token = document.getElementsByName("token")[0].value;


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

	// 	$('#txnResultDataTable').dataTable(
	// 					{
	// 						"footerCallback" : function(row, data, start, end, display) {
	// 							var api = this.api(), data;

	// 							// Remove the formatting to get integer data for summation
	// 							var intVal = function(i) {
	// 								return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
	// 							};

	// 							// Total over all pages
	// 							total = api.column(9).data().reduce(
	// 									function(a, b) {
	// 										return intVal(a) + intVal(b);
	// 									}, 0);

	// 							// Total over this page
	// 							pageTotal = api.column(9, {
	// 								page : 'current'
	// 							}).data().reduce(function(a, b) {
	// 								return intVal(a) + intVal(b);
	// 							}, 0);

	// 							// Update footer
	// 							$(api.column(9).footer()).html(
	// 									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


	// 							// Total over all pages
	// 							total = api.column(8).data().reduce(
	// 									function(a, b) {
	// 										return intVal(a) + intVal(b);
	// 									}, 0);

	// 							// Total over this page
	// 							pageTotal = api.column(9, {
	// 								page : 'current'
	// 							}).data().reduce(function(a, b) {
	// 								return intVal(a) + intVal(b);
	// 							}, 0);

	// 							// Update footer
	// 							$(api.column(8).footer()).html(
	// 									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
	// 						},
	// 						"columnDefs": [{
	// 							className: "dt-body-right",
	// 							"targets": [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]
	// 						}],
	// 							dom : 'BTrftlpi',
	// 							buttons : [
	// 									$.extend( true, {}, buttonCommon, {
	// 										extend: 'copyHtml5',
	// 										exportOptions : {
	// 											columns : [0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11,12,13,14]
	// 										},
	// 									} ),
	// 								$.extend( true, {}, buttonCommon, {
	// 										extend: 'csvHtml5',
	// 										title : 'SaleTransaction_Report_'+(new Date().getFullYear())+(monthVal>9?monthVal:'0'+monthVal)+(new Date().getDate()>9?new Date().getDate():'0'+new Date().getDate())+(new Date().getHours()>9?new Date().getHours():'0'+new Date().getHours())+(new Date().getMinutes()>9?new Date().getMinutes():'0'+new Date().getMinutes())+(new Date().getSeconds() >9?new Date().getSeconds():'0'+new Date().getSeconds()),
	// 										exportOptions : {

	// 											columns : [0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11,12,13,14]
	// 										},
	// 									} ),
	// 								{
	// 									extend : 'pdfHtml5',
	// 									orientation : 'landscape',
	// 									pageSize: 'legal',
	// 									//footer : true,
	// 									title : 'Sale Transaction Report',
	// 									exportOptions : {
	// 										columns: [0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11,12,13,14]
	// 									},
	// 									customize: function (doc) {
	// 									    doc.defaultStyle.alignment = 'center';
	// 				     					doc.styles.tableHeader.alignment = 'center';
	// 									  }
	// 								},
	// 								// Disabled print button.
	// 								/* {extend : 'print',//footer : true,title : 'Sale Transaction Report',exportOptions : {columns : [0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11]}}, */
	// 								 {
	// 									extend : 'colvis',
	// 								 	columns : [  1, 2, 4,5, 6, 7,8]
	// 								}
	// 							],

	// 						"ajax" :{

	// 							"url" : "saleTransactionSearchAction",
	// 							"type" : "POST",
	// 							"data": function (d){
	// 								return generatePostData(d);
	// 							}
	// 						},
	// 						"fnDrawCallback" : function() {

	// 								 $("#submit").removeAttr("disabled");
	// 								 $('#loading').hide();
	// 						},
	// 						 "searching": false,
	// 						 "ordering": false,

	// 						 "processing": true,
	// 					        "serverSide": true,
	// 					        "paginationType": "full_numbers",
	// 					        "lengthMenu": [[10, 25, 50], [10, 25, 50]],
	// 							"order" : [ [ 2, "desc" ] ],

	// 					        "columnDefs": [
	// 					            {
	// 					            "type": "html-num-fmt",
	// 					            "targets": 4,
	// 					            "orderable": true,
	// 					            "targets": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]
	// 					            }
	// 					        ],


	// 						"columns" : [
	// 						// 	 {
	// 						// 	"data" : "transactionId",
	// 						// 	"className" : "txnId my_class text-class",
	// 						// 	"width": "60px !important;"
	// 						// },
	// 						  {
	// 							"data" : "pgRefNum",
	// 							"className" : "pg_Ref_Num text-class"

	// 						},{
	// 							"data" : "merchants",
	// 							"className" : "merchants text-class"
	// 						}, {
	// 							"data" : "kt_datepicker_1",
	// 							"className" : "date_From text-class",
	// 							"width" : "10%"
	// 						}, {
	// 							"data" : "orderId",
	// 							"className" : "order_Id text-class"
	// 						}, {
	// 							"data" : "mopType",
	// 							"visible" : false,
	// 							"className" : "mop_Type displayNone text-class"
	// 						}, {
	// 							"data" : "paymentMethods",
	// 							"className" : "payment_Methods text-class",
	// 							"render" : function(data, type, full) {
	// 								return full['paymentMethods'] + ' ' + '-'
	// 										+ ' ' + full['mopType'];
	// 							},
	// 							"className" : "text-class"
	// 						}, {
	// 							"data" : "txnType",
	// 							"className" : "txn_Type text-class"
	// 						}, {
	// 							"data" : "status",
	// 							"className" : "status text-class"

	// 						}, {
	// 							"data" : "customerEmail",
	// 							"className" : "customer_Email text-class"
	// 						},{
	// 							"data" : "amount",
	// 							"className" : "amount text-class",
	// 							"render" : function(data){
	// 								return inrFormat(data);
	// 							}

	// 						}
	// 						, {
	// 							"data" : "totalAmount",
	// 							"className" : "total_Amount text-class",
	// 							"render" : function(data){
	// 								return inrFormat(data);
	// 							}

	// 						}, {
	// 							"data" : "currency",
	// 							"className" : "currency text-class",
	// 							"width" : "10%"
	// 						}, {
	// 							"data" : "udf4",
	// 							"visible" : ("<s:property value='%{#session.USER.UserType.name()}'/>"=="MERCHANT"||"<s:property value='%{#session.USER.UserType.name()}'/>"=="SUBADMIN")?true:false

	// 						},
	// 						 {
	// 							"data" : "udf5",
	// 							"visible" : ("<s:property value='%{#session.USER.UserType.name()}'/>"=="MERCHANT"||"<s:property value='%{#session.USER.UserType.name()}'/>"=="SUBADMIN")?true:false
	// 						},
	// 						 {
	// 							"data" : "udf6",
	// 							"visible" : ("<s:property value='%{#session.USER.UserType.name()}'/>"=="MERCHANT"||"<s:property value='%{#session.USER.UserType.name()}'/>"=="SUBADMIN")?true:false
	// 						}, {
	// 							"data": "refundButtonName",
	// 							render: function (data) {
	// 							return `<input type='button' disabled='true' name='surchargeBtn' value=`+data.replace(/ /g, '&nbsp;')+`
	// 							data-toggle='modal' data-target='#refundAccept' onclick = 'setValues(this)'
	// 							class='btn btn-lg btn-success' /> `;
	// 							}
	// 						},

	// 						{
	// 							"data" : null,
	// 							"visible" : false,
	// 							"className" : "center",
	// 							"orderable" : false,
	// 							"mRender" : function(row) {
	// 								var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
	// 								var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
	// 										if (userType == "ADMIN"|| userType == "SUBADMIN") {
	// 											return '<button class="btn btn-info btn-xs btn-block" id="btnChargeBack"   >Chargeback</button>';
	// 										} else {
	// 											return "";
	// 										}
	// 							    }
	// 						},

	// 						{
	// 							"data" : "productDesc",
	// 							"visible" : false
	// 						},  {
	// 							"data" : null,
	// 							"visible" : false,
	// 							"className" : "displayNone",
	// 							"mRender" : function(row) {
	// 								return "\u0027" + row.transactionId;
	// 							}
	// 						}

	// 						]
	// 					});

		$(document).ready(function() {

					var table = $('#txnResultDataTable').DataTable();
				$('#txnResultDataTable').on('click','.center',function(){
					var columnIndex = table.cell(this).index().column;
					var rowIndex = table.cell(this).index().row;
					var rowNodes = table.row(rowIndex).node();
					var rowData = table.row(rowIndex).data();
					var txnType1 = rowData.txnType;
					var status1 = rowData.status;

						var payId1 =  rowData.pgRefNum;
						var orderId1 = rowData.orderId;
						var txnId1 = Number(rowData.transactionId);
						document.getElementById('payIdc').value = payId1;
						document.getElementById('orderIdc').value = orderId1;
						document.getElementById('txnIdc').value = txnId1;
					    document.chargeback.submit();

			});
		});



	// }

	// function reloadTable() {
	// 	var datepick = $.datepicker;
	// 	var transFrom = $.datepicker
	// 			.parseDate('dd-mm-yy', $('#kt_datepicker_1').val());
	// 	var transTo = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_2').val());
	// 	if (transFrom == null || transTo == null) {
	// 		alert('Enter date value');
	// 		return false;
	// 	}

	// 	if (transFrom > transTo) {
	// 		alert('From date must be before the to date');
	// 		$('#loading').hide();
	// 		$('#kt_datepicker_1').focus();
	// 		return false;
	// 	}
	// 	if (transTo - transFrom > 31 * 86400000) {
	// 		alert('No. of days can not be more than 31');
	// 		$('#loading').hide();
	// 		$('#kt_datepicker_1').focus();
	// 		return false;
	// 	}
	// 	$("#submit").attr("disabled", true);
	// 	var tableObj = $('#txnResultDataTable');
	// 	var table = tableObj.DataTable();
	// 	table.ajax.reload();
	// }

	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var merchantEmailId = document.getElementById("merchant").value;
		var udf="All";
		var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
		var merchantemail = document.getElementById("merchant").value;

		var	transactionType = "Captured";

		var	paymentType = document.getElementById("paymentType").value;
		var mopType= document.getElementById("mopType").value;
		var status = "SALE";

		//var currency = document.getElementById("currency").value;
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
		// if(currency==''){
		// 	currency='ALL'
		// }
		if (paymentType == '') {
			paymentType = 'ALL'
					}
					if (mopType == '') {
						mopType = 'ALL'
					}
		var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";

		var channel = document.getElementById("Channel").value;
		var transFrom =document.getElementById('kt_datepicker_1').value;
		var transTo = document.getElementById('kt_datepicker_2').value;
		var dateFrom=convert(transFrom);
		var dateTo=convert(transTo);
		var obj = {
			transactionId : document.getElementById("pgRefNum").value,
			orderId : document.getElementById("orderId").value,
			customerEmail : document.getElementById("customerEmail").value,
			phoneNo :document.getElementById("phoneNo").value,
			mopType :mopType,
			merchantEmailId : merchantEmailId,
			transactionType : transactionType,
			paymentType : paymentType,
			channel: channel,
			status : status,
			currency : "ALL",
			dateFrom : dateFrom,
			dateTo : dateTo,
			udf:udf,
			// draw : d.draw,
			// length :d.length,
			// start : d.start,
			draw: "2",
			length: "1000",
			start: "0",
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}

	function popup(txnId) {

		var token = document.getElementsByName("token")[0].value;
		var myData = {
			token : token,
			"struts.token.name" : "token",
			"transactionId":txnId
		}
		$.ajax({
		    	url: "customerAddressAction",
		    	type : "POST",
		    	data :myData,
		    	success: function(response){
					var responseObj =  response.aaData;


					$('#sec1 td').eq(0).text(responseObj.custName ? responseObj.custName : 'Not Available');
					$('#sec1 td').eq(1).text(responseObj.custPhone ? responseObj.custPhone : 'Not Available');
					$('#sec1 td').eq(2).text(responseObj.custCity ? responseObj.custCity : 'Not Available');

					$('#sec2 td').eq(0).text(responseObj.custState ? responseObj.custState : 'Not Available');
					$('#sec2 td').eq(1).text(responseObj.custCountry ? responseObj.custCountry : 'Not Available');
					$('#sec2 td').eq(2).text(responseObj.custZip ? responseObj.custZip : 'Not Available');

					$('#address1 td').text(responseObj.custStreetAddress1 ? responseObj.custStreetAddress1 : 'Not Available');
					$('#address2 td').text(responseObj.custStreetAddress2 ? responseObj.custStreetAddress2 : 'Not Available');

					$('#sec3 td').eq(0).text(responseObj.custShipName ? responseObj.custShipName : 'Not Available');
					$('#sec3 td').eq(1).text(responseObj.custShipPhone ? responseObj.custShipPhone : 'Not Available');
					$('#sec3 td').eq(2).text(responseObj.custShipCity ? responseObj.custShipCity : 'Not Available');

					$('#sec4 td').eq(0).text(responseObj.custShipState ? responseObj.custShipState : 'Not Available');
					$('#sec4 td').eq(1).text(responseObj.custShipCountry ? responseObj.custShipCountry : 'Not Available');
					$('#sec4 td').eq(2).text(responseObj.custShipZip ? responseObj.custShipZip : '');

					$('#address3 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress1 : 'Not Available');
					$('#address4 td').text(responseObj.custShipStreetAddress2 ? responseObj.custShipStreetAddress2 : 'Not Available');

					$('#auth td').text(responseObj.internalTxnAuthentication ? responseObj.internalTxnAuthentication : 'Not Available');

				$('#popup').show();
		    	},
		    	error: function(xhr, textStatus, errorThrown){
			       alert('request failed');
			    }
		});

	};
</script>
<script type="text/javascript">
	var totalRefund = "0.00";
	function setValues(val) {


			//var row = val.parentElement.parentElement.cells;
			let tableIndex = $(val).parent().parent().index();

			var row = val;
			var cells = val.parentElement.parentElement.cells;
			var token = document.getElementsByName("token")[0].value;
			//var orderId = document.getElementById("orderIDConf").value;
			var myData = {
				token : token,
				"struts.token.name" : "token",
				"orderId": $('.order_Id').eq(tableIndex+2).html()
			}
			$.ajax({
					url: "totalRefundByOrderIdAction",
					timeout : 0,
					type : "POST",
					data :myData,
					success: function(response){
						totalRefund	= response.totalRefund;

			/* document.getElementById("pgRefConf").value = $('.pg_Ref_Num').eq(tableIndex+2).html();
			document.getElementById("orderIDConf").value = $('.order_Id').eq(tableIndex+2).html();
			document.getElementById("refundOrderIDConf").value = '';
			var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
			var amtAvailable = inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
			document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))): inrFormat(parseFloat(totalRefund.replace(/,/g, '')))+".00";
			document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
			var amtConfi = document.getElementById("amtAvail").value ;
			document.getElementById("amtConf").value = amtConfi;
			document.getElementById("currencyConf").value = $('.currency').eq(tableIndex+2).html();
			 */

			document.getElementById("pgRefConf").value = $('.pg_Ref_Num').eq(tableIndex+2).html();
			document.getElementById("orderIDConf").value = $('.order_Id').eq(tableIndex+2).html();
			document.getElementById("refundOrderIDConf").value = '';
			var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
			var Base2= inrFormat(parseFloat($('.amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
			var baseamt = Base2.toString().includes('.') ? inrFormat(parseFloat($('.amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat($('.amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
			//var baseAmt = inrFormat(parseFloat($('.amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
			var amtAvailable = inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
			document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))): inrFormat(parseFloat(totalRefund.replace(/,/g, '')))+".00";
			document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat($('.total_Amount').eq(tableIndex+2).html().replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
			var amtConfi = document.getElementById("amtAvail").value ;
			//document.getElementById("amtConf").value = baseAmt;
			document.getElementById("amtConf").value = baseamt;
			document.getElementById("currencyConf").value = $('.currency').eq(tableIndex+2).html();


			if(parseFloat("0.00") == parseFloat(amtAvailable)){
				document.getElementById("btnRefundConf").disabled = true;
			}else{
				document.getElementById("btnRefundConf").disabled = false;
			}
					},
					error: function(xhr, textStatus, errorThrown){
					  alert("Something Went Wrong");
					}
			});


	}



	</script>

<script>
// function validPgRefNum(){

// 	var pgRefValue = document.getElementById("pgRefNum").value;
// 	var regex = /^(?!0{16})[0-9\b]{16}$/;
// 	if(pgRefValue.trim() != ""){
// 		if(!regex.test(pgRefValue)) {
// 			document.getElementById("validValue").style.display= "block";
// 			document.getElementById("submit").disabled = true;
//         }
// 		else {
// 			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block"){
// 				document.getElementById("submit").disabled = false;
// 			}
// 			document.getElementById("validValue").style.display= "none";
// 		 }
// 	}
// 	else {
// 			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block"){
// 				document.getElementById("submit").disabled = false;
// 			}
// 	    document.getElementById("validValue").style.display= "none";
//     }
// }

// function validateCustomerEmail(emailField){

// 	var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
//   if (emailField.value !== "") {
//     if (reg.test(emailField.value) == false)
//     {
// 		document.getElementById("validEamilValue").style.display= "block";
// 		document.getElementById("submit").disabled = true;
// 	}else{
// 			if(document.getElementById("validValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block"){
// 				document.getElementById("submit").disabled = false;
// 			}
// 	    document.getElementById("validEamilValue").style.display= "none";
// 	}
//   }else{
// 			if(document.getElementById("validValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block"){
// 				document.getElementById("submit").disabled = false;
// 			}
// 	    document.getElementById("validEamilValue").style.display= "none";
//   }

// }

function removeSpaces(fieldVal){
	setTimeout(function() {
	var nospacepgRefVal = fieldVal.value.replace(/ /g, "");
	fieldVal.value = nospacepgRefVal;
	}, 400);
}

// function valdPhoneNo() {
//     var phoneElement = document.getElementById("phoneNo");
//     var value = phoneElement.value.trim();
//     if (value.length > 0) {
//         var phone = phoneElement.value;
//         var phoneexp = /^[0-9]{8,13}$/;
//         if (!phone.match(phoneexp)) {
//             document.getElementById('invalid-phone').innerHTML = "Please enter valid Phone";

//             return false;
//         } else {
//             document.getElementById('invalid-phone').innerHTML = "";
//             return true;
//         }
//     } else {
//         phoneElement.focus();
//         document.getElementById('invalid-phone').innerHTML = "";
//         return true;
//     }
// }

function validateOrderIdvalue(orderId){
setTimeout(function() {
	var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+.]+$/;
  if (orderId.value !== "") {
    if (orderIdreg.test(orderId.value) == false)
    {
		document.getElementById("validOrderIdValue").style.display= "block";
		document.getElementById("submit").disabled = true;
	}else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
			}
	    document.getElementById("validOrderIdValue").style.display= "none";

	}
  }else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
			}
	    document.getElementById("validOrderIdValue").style.display= "none";
  }
}, 400);
}
function validateOrderIdvaluePopup(refundOrderIDConf){
setTimeout(function() {
	var refundOrderIDConfreg =/^[0-9a-zA-Z\b\_-\s\+.]+$/;
  if (refundOrderIDConf.value !== "") {
    if (refundOrderIDConfreg.test(refundOrderIDConf.value) == false)
    {
		document.getElementById("validOrderIdValuePopup").style.display= "block";
		document.getElementById("btnRefundConf").disabled = true;
	}else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"){
				document.getElementById("btnRefundConf").disabled = false;
			}
	    document.getElementById("validOrderIdValuePopup").style.display= "none";

	}
  }else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"){
				document.getElementById("btnRefundConf").disabled = false;
			}
	    document.getElementById("validOrderIdValuePopup").style.display= "none";
  }
}, 400);
}
function validateOrderId(event) {
	  var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	    if (!regex.test(key)) {
	       event.preventDefault();
	       return false;
	    }
}
function validateOrderIdPopup(event) {
	  var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	    if (!regex.test(key)) {
	       event.preventDefault();
	       return false;
	    }
}
function refundFunction(operation) {
	var ordId = document.getElementById("orderIDConf").value;
	var refOrdId = document.getElementById("refundOrderIDConf").value;
	var pgRef = document.getElementById("pgRefConf").value;
	var amt = document.getElementById("amtConf").value.replace(/,/g, '').trim();
	var currency = document.getElementById("currencyConf").value;
	var token = document.getElementsByName("token")[0].value;
	var availAmt = document.getElementById("amtAvail").value.replace(/,/g, '').trim();
	if (amt == '' || amt < 0){
		alert("Please enter correct amount!");
		return false;
	}

	if(parseFloat(amt) > parseFloat(availAmt)){
		alert("Please check the Amount field, Refund amount More than the Available amount");
		return false;
	}

	document.getElementById("btnRefundConf").disabled = true;
	 $.ajax({
		type: "POST",
		url:"refundFromCrm",
		timeout : 0,
		data:{"orderId":ordId,"currencyCode":currency,"refundOrderId":refOrdId,"refundAmount":amt,"pgRefNum":pgRef,"token":token,"struts.token.name": "token",},
		success:function(data){
			debugger
			// document.getElementById("loadingInner").style.display = "none";
			// document.getElementById("btnRefundConf").disabled = false;
			var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
			if(null!=response){
				alert(response);
			}
			$("#refundAccept").modal("hide");
		},
		error:function(data){

			//document.getElementById("loadingInner").style.display = "none";
			document.getElementById("btnRefundConf").disabled = false;
			alert("Unable to process refund");
		}

	});
}
function isNumberKeyAmount(evt) {
		const charCode = (event.which) ? event.which : event.keyCode;
          if (charCode > 31 &&  (charCode < 48 || charCode > 57) && charCode!=46 ) {
            return false;
			return true;
          }
}
</script>
<style>
.adf-control {
	display: inline !important;
	width: 60% !important;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
}

.dataTables_wrapper {
	position: relative;
	clear: both;
	*zoom: 1;
	zoom: 1;
	margin-top: -30px;
}

.input-control {
	display: block;
	width: 107%;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	margin-left: -5px;
}

.multiselect {
	width: 170px;
	display: block;
	margin-left: -10px;
}

.selectBox {
	position: relative;
}

#checkboxes {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 3px;
	margin-right: 5px;
}

#checkboxes label {
	width: 66%;
	font-weight: 600;
}

#checkboxes input {
	width: 30%;
}

.selectBox select {
	width: 100%;
}

#checkboxes1 {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes1 label {
	width: 74%;
}

#checkboxes1 input {
	width: 18%;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
}

table.dataTable thead th {
	padding: 4px 15px !important;
}

#summaryReportDataTable {
	text-align: center;
}

#summaryReportCountDataTable {
	text-align: center;
}

.dataTables_length select option:last-child {
	display: none !important;
}

.boxheadingsmall th {
	text-align: center !important;
}

.multiselect {
	width: 100%;
	margin-left: 0;
}

.selectBox select {
	width: 100%;
}

.input-control select option {
	width: 100%;
}

.submit-btn {
	background-color: #496cb6;
	display: block;
	width: 100%;
	height: 30px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #fff;
	border: 1px solid #ccc;
	border-radius: 4px;
	margin-top: 15px;
	margin-bottom: -20px;
}

.odd {
	background-color: #e6e6ff !important;
}

table.dataTable thead .sorting {
	background: none !important;
}

.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #ccc;
	position: relative;
	background: #ddd;
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

}
<!--
--






-CSS FOR COLLAPSE DROPDOWN DESIGN---->.select2-container--default {
	display: none;
}

.btnActive {
	background: #496cb6 !important;
	color: #fff !important;
}

.newteds .newround {
	border: none;
	padding: 8px 34px;
	background: #d2d2d2;
	color: #6b6b6b;
	margin-top: 10px;
}

.newteds .newround:last-child {
	margin-right: 186px;
}

#dvApprovedAmount {
	font-size: 13px; <!--
	padding: 8px 0 2px 0;
	-->
}

.col-xs-5ths, .col-sm-5ths, .col-md-5ths, .col-lg-5ths {
	position: relative;
	min-height: 1px;
	padding-right: 0px;
	padding-left: 40px;
}

.col-xs-5ths {
	width: 20%;
	float: left;
}

.panel-right h3 {
	font-size: 13px !important;
}

@media ( min-width : 768px) {
	.col-sm-5ths {
		width: 20%;
		float: left;
	}
}

@media ( min-width : 992px) {
	.col-md-5ths {
		width: 24%;
		float: left;
	}
}

@media ( min-width : 1200px) {
	.col-lg-5ths {
		width: 20%;
		float: left;
	}
}

.collapseHead {
	color: black;
	font-weight: 700;
	font-size: 13px;
}

.newDiv {
	width: 98%;
	height: 15px;
	background: #e6e6e6;
	margin-left: 12px;
	border: 1px solid #d9d9d9;
	border-radius: 3px; <!--
	padding: 6px 10px;
	-->
}

.arrowClass {
	float: right;
	margin-right: 15px;
	color: black;
}

.animateArrow {
	color: white;
	-webkit-transform: rotate(180deg);
	-moz-transform: rotate(180deg);
	-o-transform: rotate(180deg);
	-ms-transform: rotate(180deg);
	transform: rotate(180deg);
}

.refundButtonCls {
	background: #496cb6;
	border: none;
	text-align: right !important;
	color: white;
	border-radius: 3px;
	font-size: 12px;
}

#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

.modal-content {
	padding: 10px 20px !important;
}

button#btnRefundConf {
	margin-left: -19px !important;
}

#loading-image {
	position: absolute;
	top: 40%;
	left: 55%;
	z-index: 100;
	width: 10%;
}

#loadingInner {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image-inner {
	position: absolute;
	top: 33%;
	left: 48%;
	z-index: 100;
	width: 7%;
}

button.dt-button, div.dt-button, a.dt-button {
	font-size: 14px;
}
/* @media (max-width: 768px) {
		#ui-datepicker-div{
		  position: absolute !important;
		  top: 600px !important;
		  left:60px !important;
		}
		} */
.dt-buttons.btn-group.flex-wrap {
	display: none !important;
}

#kt_datatable_vertical_scroll_filter {
	display: none !important;
}
/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button, input::-webkit-inner-spin-button {
	-webkit-appearance: none;
	margin: 0;
}

/* Firefox */
input[type=number] {
	-moz-appearance: textfield;
}
</style>
<script>
	function inputKeydownevent(event,input){

		var invalidChars = ["-","+","e"];
		if(input=='pgRefNum'){
			if (invalidChars.includes(event.key))
{
event.preventDefault();
}
}

if(input=='orderId'){
if  (invalidChars.includes(event.key))
{
event.preventDefault();
}
}
		if(input=='customerEmail'){

}
if(input=='phoneNo'){
if(invalidChars.includes(event.key))
{
event.preventDefault();
}
}
		}


	</script>
	<script>
	function getTranscationReport(){
		var reportType = document.getElementById("transcation").value;
	    if(!reportType){
            return;
        }
		var urls = new URL(window.location.href);

		var domain = urls.origin+"/crm/jsp/"+reportType;
		document.getElementById("transction").action = domain;
	    const form = document.getElementById('transction');
	    form.submit();

			}


	</script>
</head>
<body id="mainBody">


<form id ="transction" name="refundDetails" action="">

	</form>
	<!-- <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width: 70px; height: 70px;"
			src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> -->
	<div class="modal" id="refundAccept" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<!-- <div id="loadingInner" display="none">
			<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
				alt="BUSY..." />
		</div> -->
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Refund
					Instruction</div>
				<div class="modal-body">
					Please Verify the details before submission.

					<table class="table">
						<tr>
							<th>Pg Ref Num</th>
							<td id="pgRefConfTd">
							<td id="otpMerchSuraccept"><input id="pgRefConf"
								readonly="true" class="input-control" autocomplete="off"
								type="text" name="pgRefConf"></td>
							</td>
						</tr>
						<tr>
							<th>Order ID</th>
							<td id="orderIDConfTd">
							<td id="otpMerchSuraccept"><input id="orderIDConf"
								readonly="true" class="input-control" autocomplete="off"
								type="text" name="orderIDConf"></td>
							</td>
						</tr>

						<tr>
							<th>Currency</th>
							<td id="currencyConfTd">
							<td id="otpMerchSuraccept"><input id="currencyConf"
								readonly="true" class="input-control" autocomplete="off"
								type="text" name="currencyConf"></td>
							</td>
						</tr>
						<tr>
							<th>Refund Order ID</th>
							<td id="refundOrderIDConfTd">
							<td id="otpMerchSuraccept"><input id="refundOrderIDConf"
								placeholder="< Optional >" class="input-control"
								autocomplete="off" type="text" name="refundOrderIDConf"
								onKeyDown="validateOrderIdvaluePopup(this);"
								onkeypress="return validateOrderIdPopup(event);"
								ondrop="return false;"
								onpaste="validateOrderIdvaluePopup(this);" maxlength="50"></td>
							<br>
							<span id="validOrderIdValuePopup"
								style="color: red; display: none;">Please Enter Valid
								orderId</span>
							</td>

						</tr>
						<tr>
							<th>Refunded Amount</th>
							<td id="amtRefTd">
							<td id="otpMerchSuraccept"><input id="amtRef"
								readonly="true" class="input-control" autocomplete="off"
								type="text" name="amtRef"></td>
							</td>
						</tr>
						<tr>
							<th>Available Amount</th>
							<td id="amtAvailTd">
							<td id="otpMerchSuraccept"><input id="amtAvail"
								readonly="true" class="input-control" autocomplete="off"
								type="text" name="amtAvail"></td>
							</td>
						</tr>
						<tr>
							<th>Enter Refund Amount</th>
							<td id="amtConfTd">
							<td id="otpMerchSuraccept"><input id="amtConf"
								class="input-control" autocomplete="off" type="text"
								name="amtConf" onkeypress="return isNumberKeyAmount(event)"></td>
							</td>
						</tr>


					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-primary"
						id="btnRefundConf" onClick='refundFunction("accept")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						id="btnRefundCancel" data-dismiss="modal" onclick="cancelbutton()">Cancel</button>
				</div>
			</div>
		</div>
	</div>

<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<form action="" class="form mb-15" method="post"
					id="sale_captured_form">
					<s:hidden name="token" value="%{#session.customToken}" />
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class=""> Transcation Report .</span>
											</label>

											<select name="currency" id="transcation" data-control="select2" data-hide-search="true" onchange="getTranscationReport()" class="form-select form-select-solid">
												<option value="">Select TXN Report</option>
												<option selected value="saleTransactionSearch">Sale Captured</option>
												<option value="settledTransactionSearch">Settled Report</option>


											</select>

										</div>



									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->



										<div
											class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end">
											<%-- <button type="submit" id="sale_captured_submit" class="btn w-100 w-md-25 btn-primary">
													<span class="indicator-label">Submit</span>
													<span class="indicator-progress">Please wait...
														<span
															class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button> --%>


										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</form></div></div>
	<div class="content flex-column" id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Sale
						Captured</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Transaction Reports</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Sale Captured</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->
		<!--begin::Post-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<form action="" class="form mb-15" method="post"
					id="sale_captured_form">
					<s:hidden name="token" value="%{#session.customToken}" />
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">PG REF No.</span>
											</label>

											<s:textfield type="number" min="0"
												class="form-control form-control-solid" id="pgRefNum"
												name="pgrefnumber" maxlength="17"
												onkeydown="inputKeydownevent(event,'pgRefNum')"
												oninput="if(value.length>16)value=value.slice(0,16)" />

										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Order ID</span>
											</label>
											<s:textfield type="text"
												class="form-control form-control-solid" id="orderId"
												name="orderid"
												onkeydown="inputKeydownevent(event,'orderId')"
												oninput="if(value.length>30)value=value.slice(0,30)" />
										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Customer Email</span>
											</label>
											<!--end::Label-->
											<s:textfield type="email" id="customerEmail"
												class="form-control form-control-solid" name="emailid"
												onkeydown="inputKeydownevent(event,'customerEmail')" />
										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row" style="display:none;">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Phone Number</span>
											</label>
											<!--end::Label-->
											<input type="number" id="phoneNo" min="0"
												class="form-control form-control-solid" name="phonenumber"
												onkeydown="inputKeydownevent(event,'phoneNo')"
												oninput="if(value.length>10)value=value.slice(0,10)" />
										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Captured From</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg"> <path
														opacity="0.3"
														d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
														fill="currentColor" /> <path
														d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
														fill="currentColor" /> <path
														d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
														fill="currentColor" /> </svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input class="form-control form-control-solid ps-12"
													placeholder="Select a date" name="dateFrom"
													id="kt_datepicker_1" />
												<!--end::Datepicker-->
											</div>
										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Captured To</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg"> <path
														opacity="0.3"
														d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
														fill="currentColor" /> <path
														d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
														fill="currentColor" /> <path
														d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
														fill="currentColor" /> </svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input class="form-control form-control-solid ps-12"
													placeholder="Select a date" name="dateTo"
													id="kt_datepicker_2">
												<!--end::Datepicker-->
											</div>
										</div>
										<div class="col-md-4 fv-row">
											<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Merchant</span>
												</label>
											</s:if>


											<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
												<s:select name="merchant"
													class="form-select form-select-solid adminMerchants"
													id="merchant" headerKey="" headerValue="ALL"
													list="merchantList" listKey="emailId"
													listValue="businessName" autocomplete="off"
													data-control="select2" />

											</s:if>
											<s:else>
												<s:select name="merchant"
													class="form-select form-select-solid adminMerchants d-none"
													id="merchant" list="merchantList" listKey="emailId"
													data-control="select2" listValue="businessName"
													autocomplete="off" />
											</s:else>

										</div>


									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->

										<!-- <div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Currency</span>
											</label> -->
											<!--end::Label-->
											<!-- <s:select name="currency" id="currency" headerValue="ALL"
												data-control="select2" headerKey="" list="currencyMap"
												class="form-select form-select-solid" /> -->
											<!-- <select name="currency" id="currency" data-control="select2" data-hide-search="true" class="form-select form-select-solid">
												<option value="">ALL</option>
												<option value="784">AED</option>
												<option value="124">CAD</option>
												<option value="036">AUD</option>
												<option value="356">INR</option>
												<option value="840">USD</option>
												<option value="978">EUR</option>
												<option value="826">GBP</option>

											</select> -->



										<!-- </div> -->
										<!-- <div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Payment Method</span>
											</label>


											<s:select headerKey="" headerValue="ALL"
												data-control="select2" class="form-select form-select-solid"
												list="@com.pay10.commons.util.PaymentTypeUI@values()"
												listValue="name" listKey="code" name="paymentMethod"
												id="paymentMethod" autocomplete="off" value=""
												onchange="getMopType(this.value,'mopType')" />

										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">MOP Type</span>
											</label>

											<s:select name="mopType" id="mopType" headerValue="ALL"
												headerKey="ALL"
												list="@com.pay10.commons.util.MopTypeUI@values()"
												listValue="name" listKey="code" data-control="select2"
												class="form-select form-select-solid" />

										</div> -->

										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Channel</span>
											</label>
											<!--end::Label-->
											<select name="Channel" id="Channel"
												class="form-select form-select-solid adminMerchants"
												onchange="getPaymentType()">
												<option value="Fiat">Fiat</option>
											</select>
										</div>

										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Payment Method</span>
											</label>
											<!--end::Label-->
											<select name="paymentType" id="paymentType"
												onchange="getMopType(this.value,'mopType')"
												class="form-select form-select-solid adminMerchants">

											</select>
										</div>
										<div class="col-md-4 fv-row" id="mopTypeCol">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">MOP Type</span>
											</label>
											<!--end::Label-->

											<select name="mopType" id="mopType"
												class="form-select form-select-solid adminMerchants">
												<option value="ALL">ALL</option>

											</select>
										</div>


									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->

										<div class="col-md-4 fv-row" id="bses">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Filter</span>
											</label>
											<select name="udf4"
												class="form-select form-select-solid adminMerchants"
												id="udf4">
												<option value="All" selected="selected">All</option>
												<option value="energy">Energy</option>
												<option value="postpaid">Postpaid</option>
												<option value="enforcement">Enforcement</option>
											</select>
										</div>
										<div class="col-md-4 fv-row">
										<s:if
												test="%{#session.USER_TYPE.name()=='MERCHANT'}">


												<select name="udf4"
													class="form-select form-select-solid adminMerchants" id="udf4">
												<option value="energy">Energy</option>
												<option value="power">power</option>
												</select>

											</s:if>
										</div>
										<div
											class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end">
											<%-- <button type="submit" id="sale_captured_submit" class="btn w-100 w-md-25 btn-primary">
													<span class="indicator-label">Submit</span>
													<span class="indicator-progress">Please wait...
														<span
															class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button> --%>

											<button type="button" class="btn w-100 w-md-25 btn-primary"
												id="btnRefundConf1">Search</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</form>
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8 justify-content-end">

									<div class="col-lg-4 col-sm-12 col-md-6">
										<select name="currency" data-control="select2"
											data-placeholder="Actions" id="actions11"
											class="form-select form-select-solid actions"
											data-hide-search="true" onchange="myFunction();">
											<option value="">Actions</option>
											<option value="copy">Copy</option>
											<option value="csv">CSV</option>
										<!--	<option value="pdf">PDF</option> -->
										</select>
									</div>
									<div class="col-lg-4 col-sm-12 col-md-6">
										<div class="dropdown1">
											<button
												class="form-select form-select-solid actions dropbtn1">Customize
												Columns</button>
											<div class="dropdown-content1">
												<a class="toggle-vis" data-column="0">PG REF No.</a>
												<a class="toggle-vis" data-column="1">Merchant Name</a>
												<a class="toggle-vis" data-column="2">Date</a>
												<a class="toggle-vis" data-column="3">Order ID</a>
												<a class="toggle-vis" data-column="4">MOP Method</a>
												<a class="toggle-vis" data-column="5">TXN Type</a>
												<a class="toggle-vis" data-column="6">Status</a>
												<a class="toggle-vis" data-column="7">Customer Email</a>
												<a class="toggle-vis" data-column="8">Base Amount</a>
												<a class="toggle-vis" data-column="9">Total Amount</a>
												<a class="toggle-vis" data-column="10">Currency</a>
												
											</div>
										</div>
									</div>
								</div>
								<div class="row g-9 mt-9">
									<table id="searchAgentDataTable"
										class="table table-striped table-row-bordered gy-5 gs-7">
										<thead>
											<tr class="fw-bold fs-6 text-gray-800">
												<th class="min-w-90px">PG REF No.</th>
												<th scope="col">Merchant Name</th>
												<th scope="col">Date</th>
												<th scope="col">Order ID</th>
												<th scope="col">MOP Method</th>
												<th scope="col">TXN Type</th>
												<th scope="col">Status</th>
												<th scope="col">Customer Email</th>
												<th scope="col">Base Amount</th>
												<th scope="col">Total Amount</th>
												<th scope="col">Currency</th>
												<!-- <th scope="col">Refund id</th> -->
												<s:if
													test="%{(#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN')}">
													<!-- 									<th style="text-align:center;">Chargeback</th> -->
												</s:if>
												<s:else>
													<!-- 									<th style='text-align: center'></th> -->
												</s:else>
											</tr>
										</thead>
										<tfoot>
											<tr class="fw-bold fs-6 text-gray-800">
												<!-- <th></th> -->
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>


												<s:if
													test="%{(#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN')}">
													<!-- 									<th style="text-align:center;">Chargeback</th> -->
												</s:if>
												<s:else>
													<!-- 									<th style='text-align: center'></th> -->
												</s:else>
											</tr>
										</tfoot>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--end::Container-->
		</div>
		<!--end::Post-->
	</div>

	<s:form name="chargeback" action="chargebackAction">
		<s:hidden name="orderId" id="orderIdc" value="" />
		<s:hidden name="payId" id="payIdc" value="" />

		<s:hidden name="txnId" id="txnIdc" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>


	<s:form name="refundDetails" action="refundConfirmAction">
		<s:hidden name="orderId" id="orderIdr" value="" />
		<s:hidden name="payId" id="payIdr" value="" />
		<s:hidden name="transactionId" id="txnIdr" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>

	<script type="text/javascript">

var urls = new URL(window.location.href);
				var domain = urls.origin;

				var channel = $("#Channel").val().toUpperCase();



				$.ajax({
					url: domain + "/crmws/PaymentMethod/PaymentType/"+channel,
					//url: "http://localhost:8081/PaymentMethod/PaymentType/" + channel,
					type: 'GET',

					contentType: "application/json",
					success: function (data) {
						const selectElement = document.getElementById('paymentType');
						const option = document.createElement('option');
						option.value = "ALL";
						option.text = "ALL";
						selectElement.appendChild(option);

						if (data.respmessage == "Successfully") {
							var data = data.multipleResponse;
							Object.keys(data).forEach(key => {
								const value = data[key];

								console.log(key + "\t" + value);

								const option = document.createElement('option');
								option.value = key;
								option.text = value;
								selectElement.appendChild(option);

							}
							);
						}
					},
					error: function (data, textStatus, jqXHR) {


					}
				});
				if(channel!="FIAT"){
						document.getElementById("mopTypeCol").style.display = "none";
					}
				function getPaymentType() {
					debugger
					var channel = $("#Channel").val().toUpperCase();



					const selectElement = document.getElementById('paymentType');

					// Remove all child elements (options) from the select tag
					while (selectElement.firstChild) {
						selectElement.removeChild(selectElement.firstChild);
					}

					$.ajax({
						url: domain + "/crmws/PaymentMethod/PaymentType/"+channel,
						//url: "http://localhost:8081/PaymentMethod/PaymentType/" + channel,
						type: 'GET',

						contentType: "application/json",
						success: function (data) {
							const selectElement = document.getElementById('paymentType');
							if (data.respmessage == "Successfully") {
								var data = data.multipleResponse;
								const option = document.createElement('option');
								option.value = "ALL";
									option.text = "ALL";
									selectElement.appendChild(option);
								Object.keys(data).forEach(key => {
									const value = data[key];

									console.log(key + "\t" + value);

									const option = document.createElement('option');
									option.value = key;
									option.text = value;
									selectElement.appendChild(option);

								}
								);
							}
						},
						error: function (data, textStatus, jqXHR) {


						}
					});

					if(channel!="FIAT"){
						document.getElementById("mopTypeCol").style.display = "none";
					}else{
						document.getElementById("mopTypeCol").style.display = "block";
					}
				}
$(document).ready(function(){
	$('#closeBtn').click(function(){
		$('#popup').hide();
	});
});
$('#txnResultDataTable').on( 'draw.dt', function () {
	enableBaseOnAccess();
} );
function enableBaseOnAccess() {
	setTimeout(function(){
		if ($('#saleTransactionSearch').hasClass("active")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["saleTransactionSearch"];
			var refunds = document.getElementsByName("surchargeBtn");
			if (access.includes("Refund")) {
				for (var i = 0; i < refunds.length; i++) {
					var refund = refunds[i];
					refund.disabled=false;
				}
			} else {
				for (var i = 0; i < refunds.length; i++) {
					var refund = refunds[i];
					refund.remove();
				}
			}
		}
	},500);
}


var table;

function myFunction() {
	var x = document.getElementById("actions11").value;
	if(x=='csv'){
		document.querySelector('.buttons-csv').click();
	}
	if(x=='copy'){
		document.querySelector('.buttons-copy').click();
	}
	/*
	if(x=='pdf'){
		document.querySelector( '.buttons-pdf').click();
	}
	*/

	// document.querySelector('.buttons-excel').click();
	// document.querySelector('.buttons-print').click();


}
</script>
	<script>

$("#kt_datatable_vertical_scroll").DataTable({
	 	scrollY: true,
		scrollX: true,

	 });

</script>
	<script>
	"use strict";
	var KTCareersApply = function () {
		var t, e, i;
		return {
			init: function () {
				i = document.querySelector("#sale_captured_form"),
					t = document.getElementById("btnRefundConf1"),
					e = FormValidation.formValidation(i, {
						fields: {
							pgrefnumber: {
											validators: {
												//notEmpty: {
												// 	message: "PG Ref No is required"
												// },
												stringLength: {
													max: 16,
													min: 16,
													message: 'Please Enter 16 Digit PG Ref No.',
												}
											}
										},

							emailid: {
											 validators: {
											 	//notEmpty: {
											// 		message: "Name is required"
											 //	},
												 stringLength: {
												 	max: 60,
												 	message: 'email Id should be less than 60 characters.',
													 btnDisable:true
												},
												callback: {
													callback: function (input) {
														if (input.value.length==0) {
															document.getElementsByClassName("invalid-feedback")[2].style.display='none';
														}else{
															document.getElementsByClassName("invalid-feedback")[2].style.display='block';

															if (!input.value.match(/^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/)) {
															return {
																valid: false,
																message: 'Please enter a valid email.',
																btnDisable:true
															};
														}
														else {
															return {
																valid: true,
																btnDisable:false
															}
														}

														}

													}
												}
											}
										},
										orderid: {
											 validators: {
											 	//notEmpty: {
											// 		message: "Name is required"
											 //	},
												 stringLength: {
												 	max: 30,
												 	message: 'Order Id should be less than 30 characters.',
													 btnDisable:true
												},
												callback: {
													callback: function (input) {
														if (input.value.match(/[!\@\^\&\/\\#,\|+()$~%.'":*?<>{}]/)) {
															return {
																valid: false,
																message: 'Special characters not allowed.',
																btnDisable:true
															};
														} else {
															return {
																valid: true,
																btnDisable:false
															}
														}
													}
												}
											}
										},
										phonenumber: {
											validators: {
												// notEmpty: {
												// 	message: "Phone Number is required"
												//},

												stringLength: {
													max: 10,
													min: 10,
													message: 'Please enter valid phone number.',
													btnDisable:true
												}
											}
										},


									},


									plugins: {
										trigger: new FormValidation.plugins.Trigger,
										 bootstrap: new FormValidation.plugins.Bootstrap5({
										 	rowSelector: ".fv-row",
											eleInvalidClass: "",
										 	eleValidClass: ""
										 })
									}
					});
					t.addEventListener("click", (function (i) {
										 i.preventDefault(),

										e && e.validate().then((function (e) {
											if(e=='Invalid'){
												debugger



			}
			else{
				handleChange();
			//	document.getElementById("btnRefundConf").disabled = false;


			}
												}
									))

									// i.preventDefault(),

									// 	e && e.validate().then((function (e) {
									// 		populateDataTable();

									// 				}

									//	))
								}
								))
			}
		}
	}();
	KTUtil.onDOMContentLoaded((function () {
		KTCareersApply.init()
	}
	));
</script>


	<script>

	$("#sale_captured_submit").click(function(env) {

		reloadTable();
	});

function convert(str) {
  var date = new Date(str),
    mnth = ("0" + (date.getMonth() + 1)).slice(-2),
    day = ("0" + date.getDate()).slice(-2);
  //return [date.getFullYear(), mnth, day].join("-");
  return [day,mnth,date.getFullYear() ].join("-");
}
var transFrom ;
		var transTo ;
		var dateFrom;
		var dateTo;
		var token ;
function reloadTable(){
	debugger

		 transFrom = flatpickr("#kt_datepicker_1", {}).selectedDates[0];
		 transTo = flatpickr("#kt_datepicker_2", {}).selectedDates[0];
		 dateFrom=convert(transFrom);
		 dateTo=convert(transTo);
		 token = document.getElementsByName("token")[0].value;
		 var tableObj = $('#kt_datatable_vertical_scroll');
		 var table = tableObj.DataTable();
		 table.destroy();
		KTDatatablesServerSide.init();

// $.post("saleTransactionSearchAction",{
// 	transactionId: "",
// orderId: "",
// customerEmail:"",
// phoneNo: "",
// mopType: "ALL",
// merchantEmailId: "ALL",
// transactionType: "Captured",
// paymentType: "ALL",
// status: "SALE",
// currency: "ALL",
// dateFrom: dateFrom,
// dateTo: dateTo,
// draw: "2",
// length: "10",
// start: "0",
// token : token,
// "struts.token.name" : "token",
// },function(result){
// 	//alert(result);
// 	var originalJsonData=JSON.parse(JSON.stringify(result));
// 	// alert(originalJsonData);
// //$('#kt_datatable_vertical_scroll').DataTable().ajax.data(originalJsonData).reload(true);
// // $('#kt_datatable_vertical_scroll').DataTable('destroy');
// KTDatatablesServerSide.init();
// // $('#kt_datatable_vertical_scroll').DataTable('reload');

// //alert(result);
// });


		// $("#sale_captured_submit").attr("disabled", true);
		// var tableObj = $('#kt_datatable_vertical_scroll');
		// var table = tableObj.DataTable();
		// table.ajax.reload();
}


"use strict";

// Class definition
var KTDatatablesServerSide = function () {
    // Shared variables
    var table;
    var dt;
    var filterPayment;

    // Private functions
    var initDatatable = function () {
        dt = $("#kt_datatable_vertical_scroll").DataTable({
			dom: 'Brtipl',

  buttons: [
	  {
			extend: 'copy',
			exportOptions: {
				columns: ':visible'
			}
		},
		{
			extend: 'csv',
			exportOptions: {
				columns: ':visible'
			}
		},
		{
			extend: 'pdf',
			exportOptions: {
				columns: ':visible'
			}
		},
  'excel', 'print'
  ],
  			scrollY: true,
			scrollX: true,
            searchDelay: 500,
            processing: false,
          //  serverSide: true,
            order: [[5, 'desc']],
           paging: true,

            select: {
                style: 'multi',
                selector: 'td:first-child input[type="checkbox"]',
                className: 'row-selected'
            },
             ajax: {
                 url: "saleTransactionSearchAction",
				 type: 'remote',
				 method:'POST',
				 data:{
					transactionId: "",
					orderId: "",
customerEmail:"",
phoneNo: "",
mopType: "ALL",
merchantEmailId: "ALL",
transactionType: "Captured",
paymentType: "ALL",
status: "SALE",
currency: "ALL",
dateFrom: dateFrom,
dateTo: dateTo,
draw: "2",
length: "10",
start: "0",
token : token,
"struts.token.name" : "token"

				 },

             },
            columns: [
                { data: 'pgRefNum' },
                { data: 'customerName' },
                { data: 'createDate' },
                { data: 'orderId' },
				{ data:"paymentMethods",
							 render: function(data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
											+ ' ' + full['mopType'];
								}
					},
                { data: 'txnType' },
                { data: 'status' },
				{ data: 'customerEmail' },
                { data: 'amount' },
                { data: 'totalAmount' },
                { data: 'currency' },
            ],
            columnDefs: [


            ],

        });

        table = dt.$;

        // Re-init functions on every table re-draw -- more info: https://datatables.net/reference/event/draw
        dt.on('draw', function () {
            initToggleToolbar();
            toggleToolbars();
            handleDeleteRows();
            KTMenu.createInstances();
			document.getElementById("searchAgentDataTable_wrapper").style.marginTop = "14px";
			document.getElementsByClassName(".dt-buttons.btn-group.flex-wrap").style.display="none";
			document.getElementById("kt_datatable_vertical_scroll_filter").style.display="none";

        });
    }

    // Search Datatable --- official docs reference: https://datatables.net/reference/api/search()
    var handleSearchDatatable = function () {
        const filterSearch = document.querySelector('[data-kt-docs-table-filter="search"]');
        filterSearch.addEventListener('keyup', function (e) {
            dt.search(e.target.value).draw();
        });
    }

    // Filter Datatable
    var handleFilterDatatable = () => {
        // Select filter options
        filterPayment = document.querySelectorAll('[data-kt-docs-table-filter="payment_type"] [name="payment_type"]');
        const filterButton = document.querySelector('[data-kt-docs-table-filter="filter"]');

    }

    // Delete customer
    var handleDeleteRows = () => {
        // Select all delete buttons
        const deleteButtons = document.querySelectorAll('[data-kt-docs-table-filter="delete_row"]');

        deleteButtons.forEach(d => {
            // Delete button on click
            d.addEventListener('click', function (e) {
                e.preventDefault();

                // Select parent row
                const parent = e.target.closest('tr');

            })
        });
    }

    // Reset Filter
    var handleResetForm = () => {
        // Select reset button
        const resetButton = document.querySelector('[data-kt-docs-table-filter="reset"]');

        // Reset datatable
        resetButton.addEventListener('click', function () {
            // Reset payment type
            filterPayment[0].checked = true;

            // Reset datatable --- official docs reference: https://datatables.net/reference/api/search()
            dt.search('').draw();
        });
    }

    // Init toggle toolbar
    var initToggleToolbar = function () {
        // Toggle selected action toolbar
        // Select all checkboxes
        const container = document.querySelector('#kt_datatable_vertical_scroll');
        const checkboxes = container.querySelectorAll('[type="checkbox"]');

        // Select elements
        const deleteSelected = document.querySelector('[data-kt-docs-table-select="delete_selected"]');

        // Toggle delete selected toolbar
        checkboxes.forEach(c => {
            // Checkbox on click event
            c.addEventListener('click', function () {
                setTimeout(function () {
                    toggleToolbars();
                }, 50);
            });
        });


    }

    // Toggle toolbars
    var toggleToolbars = function () {
        // Define variables
        const container = document.querySelector('#kt_datatable_vertical_scroll');
        const toolbarBase = document.querySelector('[data-kt-docs-table-toolbar="base"]');
        const toolbarSelected = document.querySelector('[data-kt-docs-table-toolbar="selected"]');
        const selectedCount = document.querySelector('[data-kt-docs-table-select="selected_count"]');

        // Select refreshed checkbox DOM elements
        const allCheckboxes = container.querySelectorAll('tbody [type="checkbox"]');

        // Detect checkboxes state & count
        let checkedState = false;
        let count = 0;

        // Count checked boxes
        allCheckboxes.forEach(c => {
            if (c.checked) {
                checkedState = true;
                count++;
            }
        });
    }

    // Public methods
    return {
        init: function () {
            initDatatable();
            //handleSearchDatatable();
            initToggleToolbar();
            handleFilterDatatable();
            handleDeleteRows();
            handleResetForm();
        }
    }
}();



</script>
	<script type="text/javascript">
$('a.toggle-vis').on('click', function (e) {
    debugger
    e.preventDefault();
    table = $('#searchAgentDataTable').DataTable();
    // Get the column API object
    var column1 = table.column($(this).attr('data-column'));
    // Toggle the visibility
    column1.visible(!column1.visible());
    if($(this)[0].classList[1]=='activecustom'){
        $(this).removeClass('activecustom');
    }
    else{
        $(this).addClass('activecustom');
    }
});

</script>
<script type="text/javascript">
$(document).ready(function() {
	$("#bses").hide();
	var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
	var merchantemail = document.getElementById("merchant").value;
	if(merchantemail=="sunil98@gmail.com" && userType == 'Merchant'){
		$("#bses").show();
	}
});

</script>
</body>
</html>