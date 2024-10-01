<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Merchant Exception</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>

<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#merchant").select2();
});
</script>

<script type="text/javascript">
	$(document).ready(function() {

		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
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
			reloadTable();		
		});

		$(function(){
			var datepick = $.datepicker;
			var table = $('#txnResultDataTable').DataTable();
			$('#txnResultDataTable').on('click', 'td.my_class', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();
				
			});
		});
	});

	function renderTable() {
		  var merchantEmailId = document.getElementById("merchant").value;
		var table = new $.fn.dataTable.Api('#txnResultDataTable');
		
		var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
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
		var token = document.getElementsByName("token")[0].value;

		
		 var buttonCommon = {
        exportOptions: {
            format: {
                body: function ( data, column, row, node ) {
                    // Strip $ from digit column to make it numeric
                	return column === 0 ? "'"+data : (column === 1 ? "'" + data: column === 2 ? "'" + data:data);
                }
            }
        }
    };
	
		$('#txnResultDataTable').dataTable(
						{
							/* "footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(13).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(13, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(13).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');
							}, */
							 "columnDefs": [{ 
								className: "dt-body-right",
								"targets": [1,2,3,4,5,6]
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
											title : 'Merchant Exception',
											exportOptions : {
												
												columns : [':visible']
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Merchant Exception',
										exportOptions : {
											columns: [':visible']
										},
										customize: function (doc) {
										    doc.defaultStyle.alignment = 'center';
					     					doc.styles.tableHeader.alignment = 'center';
										  }
									},
									{
										extend : 'print',
										//footer : true,
										title : 'Merchant Exception',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										columns : [ 0,1, 2, 3, 4, 5, 6]
									} ],

							"ajax" :{
								
								"url" : "merchantExceptionReportAction",
								"type" : "POST",
								"data": function (d){
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
									 $("#submit").removeAttr("disabled");
							},
							 "searching": false,
							 "ordering": false,
							 "language": {
								"processing": ` <div id="loader-wrapper">
												<div class="loader" >
													<div id="progress" >
													<img src="../image/sand-clock-loader.gif">
												</div>
												</div>
												</div>`
								},
							 "processing": true,
						        "serverSide": true,
						        "paginationType": "full_numbers", 
						        "lengthMenu": [[10, 25, 50], [10, 25, 50]],
								"order" : [ [ 2, "desc" ] ],
						       
						        "columnDefs": [
						            {
						            "type": "html-num-fmt", 
						            "targets": 4,
						            "orderable": true, 
						            "targets": [0,1,2,3,4,5,6]
						            }
						        ], 

 
							"columns" : [ {
								"data" : "pgRefNo",
								"className" : "payId"
							},  {
								"data" : "txnId",
								"className" : "payId"
								
							},{
								"data" : "orderId",
								"className" : "orderId"
							}, {
								"data" : "acqId"
							}, {
								"data" : "createdDate"
							}, {
								"data" : "status"
							}, {
								"data" : "exception"
								
							}]
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
		var merchant = document.getElementById("merchant").value;
		 var acquirer = document.getElementById("acquirer").value;
		 var status = document.getElementById("status").value;
		 var dateFrom = document.getElementById("dateFrom").value;
		 var dateTo = document.getElementById("dateTo").value;
		 
		 var token  = document.getElementsByName("token")[0].value;
		if(merchant==''){
			merchant='ALL'
		}
		if(acquirer==''){
			acquirer='ALL'
		}
		if(status==''){
			status='ALL'
		}
		
		var obj = {
			merchant : merchant,
			acquirer : acquirer,
			status : status,
			dateFrom : dateFrom,
			dateTo : dateTo,
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}
</script>
<style type="text/css">
.cust {width: 24%!important; margin:0 5px !important; /*font: bold 10px arial !important;*/}
.samefnew{
	width: 11.5%!important;
    margin: 0 5px !important;
    /*font: bold 10px arial !important;*/
}
.btn {padding: 3px 7px!important; font-size: 12px!important; }
.samefnew-btn{
    width: 12%;
    float: left;
    font: bold 11px arial;
    color: #333;
    line-height: 22px;
    margin: 0;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class{
	cursor: pointer;
}
tr td.my_class:hover{
	cursor: pointer !important;
}

tr th.my_class:hover{
	color: #fff !important;
}

.cust .form-control, .samefnew .form-control{
	margin:0px !important;
	width: 100%;
}
.select2-container{
	width: 100% !important;
}
.clearfix:after{
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}
#popup{
	position: fixed;
	top:0px;
	left: 0px;
	background: rgba(0,0,0,0.7);
	width: 100%;
	height: 100%;
	z-index:999; 
	display: none;
}
.innerpopupDv{
	    width: 600px;
    margin: 80px auto;
    background: #fff;
    padding: 3px 10px;
    border-radius: 10px;
}
.btn-custom {
    margin-top: 5px;
    height: 27px;
    border: 1px solid #5e68ab;
    background: #5e68ab;
    padding: 5px;
    font: bold 12px Tahoma;
    color: #fff;
    cursor: pointer;
    border-radius: 5px;
}
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
	background: rgba(225,225,225,0.6) !important;
	width: 50% !important;
}
.invoicetable{
	float: none;
}
.innerpopupDv h2{
	    font-size: 12px;
    padding: 5px;
}
 
</style>
</head>
<body id="mainBody">	
   <div style="overflow:scroll !important;">
	<table id="mainTable" width="100%" border="0" align="center"
		cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td colspan="5" align="left"><h2>Merchant Exception</h2></td>
		</tr>
		<tr>
			<td colspan="5" align="left" valign="top"><div class="MerchBx">
					<%--<div class="clearfix">							

					 <div class="cust">
						Merchant:<br>
						<div class="txtnew">
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="" headerValue="ALL" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="" headerValue="ALL" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>
					</div> 
					</div>--%>


					<div class="clearfix">
						<div class="cust">
							Merchant:<br>
							<div class="txtnew">
								<s:if
									test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
									<s:select name="merchant" class="form-control" id="merchant"
										headerKey="" headerValue="ALL" list="merchantList"
										listKey="payId" listValue="businessName" autocomplete="off" />
								</s:if>
								<s:else>
									<s:select name="merchant" class="form-control" id="merchant"
										headerKey="" headerValue="ALL" list="merchantList"
										listKey="payId" listValue="businessName" autocomplete="off" />
								</s:else>
							</div>
						</div>
						
						<div class="cust">
						Acquirer:<br>
						<div class="txtnew">
							<s:select headerKey="" headerValue="ALL" class="form-control"
								list="@com.pay10.commons.util.AcquirerTypeUI@values()"
								listValue="name" listKey="code" name="acquirer"
									id="acquirer" autocomplete="off" value="" />
							</div>
						</div>					

						<div class="samefnew">
							Status:<br>
							<div class="txtnew">
								<s:select name="status" id="status" headerValue="ALL"
									headerKey="ALL" list="#{'PENDING':'PENDING', 'RESOLVED':'RESOLVED'}" class="form-control"
									autocomplete="off" />
							</div>
						</div>

						<div class="samefnew">
							Date From:<br>
							<div class="txtnew">
								<s:textfield type="text" id="dateFrom" name="dateFrom" class="form-control" autocomplete="off" readonly="true" />
							</div>
						</div>

						<div class="samefnew">
							Date To:<br>
							<div class="txtnew">
								<s:textfield type="text" id="dateTo" name="dateTo" class="form-control" autocomplete="off" readonly="true" />
							</div>
						</div>

						<div class="samefnew-btn">
							&nbsp;<br>
							<div class="txtnew">
								<input type="button" id="submit" value="Submit"
									class="btn btn-sm btn-block btn-success">
									
							</div>
						</div>
					</div>
				</div>

			</td>
		</tr>
		<tr>
			<td colspan="5" align="left"><h2>&nbsp;</h2></td>
		</tr>
		<tr>
			<td align="left" style="padding: 10px;">
				<div class="scrollD">
					<table id="txnResultDataTable" class="" cellspacing="0"
						width="100%">
						<thead>
							<tr class="boxheadingsmall">
								<th style='text-align: center'>Pg Ref Num</th>
								<th style='text-align: center'>Txn ID</th>
								<th style='text-align: center'>Order ID</th>
								<th style='text-align: center'>Acquirer</th>
								<th style='text-align: center'>Create Date</th>
								<th style='text-align: center'>Status</th>
								<th style='text-align: center'>Exception</th>							
								
							</tr>
						</thead>
					</table>
				</div>
			</td>
		</tr>

	</table>
  </div>	
</body>
</html>