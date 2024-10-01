<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Reco Process Display</title>
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
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>

<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

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
		});

		$(function() {
			var today = new Date();
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			
		});

		$("#refreshBtn").click(function(env) {
			reloadTable();
            renderTable();			
		});

		$(function(){
			var datepick = $.datepicker;
			var table = $('#recoDisplayDatatable').DataTable();
			$('#recoDisplayDatatable').on('click', 'td.my_class', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();
			});
		});
	});

	function renderTable() {
		var table = new $.fn.dataTable.Api('#recoDisplayDatatable');
		
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
	
		$('#recoDisplayDatatable').dataTable(
						{
							"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(1).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(1, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(1).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');
							}, 
							 "columnDefs": [{ 
								className: "dt-body-right",
								"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
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
											title : 'Reco Process Display',
											exportOptions : {
												
												columns : [':visible']
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Reco Process Display',
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
										title : 'Reco Process Display',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
									} ],

							"ajax" :{
								
								"url" : " ",       //enter action name
								"type" : "POST",
								"data": function (d){
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
									 $("#refreshBtn").removeAttr("disabled");
							},
							   "searching": false,
							   "ordering": false,
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
						            "targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
						            }
						        ], 

							"columns" : [ 
							{
							  "data" : "id"
							},{
							  "data" : "totalEntries"
							},{
							  "data" : "totalErrorEntries"
							},{
								"data" : "totalProcessedEntries"
							},{
								"data" : "totalDuplicateEntries"
							},{
								"data" : "totalMismatchEntries"
							},{
								"data" : "totalUnsettledEntries"
							},{
								"data" : "totalBlankEntries"
							},{
								"data" : "fileName"
							},{
								"data" : "status"
							},{
								"data" : "startTime"
							},{
								"data" : "endTime"
							},{
								"data" : "lastErrorStatus"
							},{
								"data" : "fileType"
							}]
						});
	}

	function reloadTable() {
		var datepick = $.datepicker;
		var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
		//$("#refreshBtn").attr("disabled", true);
		var tableObj = $('#recoDisplayDatatable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}

	function generatePostData(d) {
		var fileType = document.getElementById("fileType").value;
		var dateFrom = document.getElementById("dateFrom").value;
		var token  = document.getElementsByName("token")[0].value;
		
		var obj = {
			fileType: fileType,
			dateFrom : dateFrom,
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
.cust {
	width: 24%!important; 
	margin:0 5px !important; 
	font-weight: 700 !important;
	font-size: 13px !important;
}
.samefnew{
	width: 11.5%!important;
    margin: 0 5px !important;
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
.clearfix:after{
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
	background: rgba(225,225,225,0.6) !important;
	width: 50% !important;
}
table.dataTable thead .sorting {
    background: none !important;
}
.MerchBx{
	margin-top: 2% !important;
    margin-bottom: 5% !important;
}
</style>
</head>
<body id="mainBody">	
   <div style="overflow:scroll !important;">
	<table id="mainTable" width="100%" border="0" align="center"
		cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td colspan="5" align="left"><h2>Reco Process Display</h2></td>
		</tr>
		<tr>
			<td colspan="5" align="left" valign="top"><div class="MerchBx">
					
					<div class="clearfix">
						<div class="cust">
							File Type: <br>
							<div class="txtnew">
								<select class="form-control" id="fileType" name="fileType">
									<option>MPR</option>
									<option>Refund</option>
								</select>
							</div>
						</div>					

						<div class="cust">
							Date:<br>
							<div class="txtnew">
								<s:textfield type="text" id="dateFrom" name="dateFrom" class="form-control" autocomplete="off" readonly="true" />
							</div>
						</div>


					<div class="samefnew-btn">
						&nbsp;<br>
						<div class="txtnew">
							<input type="button" id="refreshBtn" value="Refresh" class="btn btn-sm btn-block btn-success">
						</div>
					</div>
					</div>
				</div>

			</td>
		</tr>
		
		<tr>
			<td align="left" style="padding: 10px;">
				<div class="scrollD">
					<table id="recoDisplayDatatable" class="" cellspacing="0"
						width="100%">
						<thead>
							<tr class="boxheadingsmall">
								<th style='text-align: center'>ID</th>
								<th style='text-align: center'>Total Entries</th>
								<th style='text-align: center'>Total Error Entries</th>
								<th style='text-align: center'>Total Processed Entries</th>
								<th style='text-align: center'>Total Duplicate Entries</th>
								<th style='text-align: center'>Total Mismatch Entries</th>
								<th style='text-align: center'>Total Unsettled Entries</th>	
                                <th style='text-align: center'>Total Blank Entries</th>
								<th style='text-align: center'>File Name</th>
								<th style='text-align: center'>Status</th>
								<th style='text-align: center'>Start Time</th>
								<th style='text-align: center'>End Time</th>
								<th style='text-align: center'>Last Error Status</th>
								<th style='text-align: center'>File Type</th>
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