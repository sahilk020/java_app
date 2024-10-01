<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Refund Summary Report</title>
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
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />
<style type="text/css">
.refundSummaryRpt .cust{
	width: 18%;
}
.refundSummaryRpt label{
	margin-left: 4%;
}
#checkboxes {
  display: none;
  border: 1px #dadada solid;
  height:254px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:5px;
  width: 100%;
}

#checkboxes label {
  width: 74%;
}
#checkboxes input {
  width:18%;

}
.selectBox select {
  width: 95%;
  
}
.multiselect {
    width: 100%;
    display: block;
    margin-left: -4px;
    position: relative;
}
.select2 {
	width: 100% !important;
}
.buttonDiv{
	margin: 10px 0;
}
.buttonDiv input{
	margin: auto;
    display: block;
    padding: 5px 22px;
    font-size: 12px;
}
.scrollD{
	margin-top:33px;
	width: 113%;
}
.scrollD thead, .scrollD tfoot {
	background: #496cb6;
	color:#fff;
}
#myTable, #myTable th{
	text-align: center;
}
#myTable th{
	padding: 10px;
}
.width1{
	width: 10%;
}
.width2{
	width: 12%;
}
.width3, .width4{
	width: 15%;
}
#loading {
    width: 100%;
    height: 100%;
    top: 0px;
    left: 0px;
    position: fixed;
    display: block;
    z-index: 99;
}
#loading-image {
    position: absolute;
    top: 40%;
    left: 45%;
    z-index: 100;
}
#myTable_info{
	padding-left:18px;
}

</style>
<script type="text/javascript">
$(document).ready(function(){

 
  // Initialize select2
  $("#merchants").select2();

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
			renderTable();

		});


	
	//click on submit button
	$("#submitBtn").click(function(env) {
			renderTable();
	});

	function renderTable(){
		var buttonCommon = {
			        exportOptions: {
			            format: {
			                body: function ( data, column, row, node ) {
			                    // Strip $ from salary column to make it numeric
			                    return column === 0 ? "'"+data : (column === 3 ? "'" + data: data);
			                }
			            }
			        }
			    };
		$('#loader-wrapper').show();
		var table = $('#myTable').DataTable( {
			"destroy": true,
			"bSort": false,
			dom : 'BTftlpi',
			buttons : [
								$.extend( true, {}, buttonCommon, {
										extend : 'copyHtml5',
										//footer : true,
										exportOptions : {
											columns : [':visible']
										}
									}),
									$.extend( true, {}, buttonCommon, {
										extend : 'csvHtml5',
										//footer : true,
										title : 'Refund Summary Report',
										exportOptions : {
											columns : [':visible']
										}
									}),
									{
										extend : 'pdfHtml5',
										//footer : true,
										orientation : 'landscape',
										pageSize: 'LEGAL',
										title : 'Refund Summary Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'print',
										//footer : true,
										pageSize: 'LEGAL',
										title : 'Refund Summary Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										//           collectionLayout: 'fixed two-column',
										columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 ]
									} ],
									
	        "ajax": {
               "url": "refundSummaryReportAction",
               "timeout" : "0",
  
               "data": function (d){
					return generatePostData();
				},
               /*dataSrc: function (response) {
                   console.log('response==>', response)
               },*/
               "type": "POST"
           },
           "fnDrawCallback" : function() {
					 //$("#submit").removeAttr("disabled");
					 	$('#loading').hide();	
			},
			"searching" : false,
			"processing" : true,
			"serverSide" : false,
			"paginationType" : "full_numbers",
			"lengthMenu" : [ [ 10, 25, 50, -1 ],
							[ 10, 25, 50, "All" ] ],
			"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : false,
								"targets" : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
							},
							{ "width": "8%", "targets": 3 } 
                         ],
	        "columns": [
	            { 
				"data": "acquirer" 
				},{
					"data": "paymentType",
					"className":"width2"
				},{ 
				   "data": "mop"
				},{
					"data": "txnInitiate", 
					"className" : "width3" 
				},{
					"data": "captured" 
				},{
					"data": "declined"
				},{ 
				    "data": "rejected" 
				},{
					"data": "pending" 
				},{
					"data": "error"
				},{ 
		            "data": "timeout" 
				},{ 
				   "data": "failed",
				   "className" : "width3"
				},{ 
				   "data": "invalid",
                   "className" : "width3"				   
				},{ 
				   "data": "acqDown",
				   "className" : "width2" 
				},{ 
				   "data": "failedAtAcq",
				   "className" : "width3"
				},{
					"data": "acqTimeout",
					"className" : "width4" 
				}
	        ],
	       "footerCallback": function ( row, data, start, end, display ) {
            var api = this.api(), data;
 
            // Remove the formatting to get integer data for summation
            var intVal = function ( i ) {
                return typeof i === 'string' ?
                    i.replace(/[\$,]/g, '')*1 :
                    typeof i === 'number' ?
                        i : 0;
            };
 
          
           
            // Total over this page colomn no 3
            pageTotal = api
                .column( 3, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 3 ).footer() ).html( // Update footer
                pageTotal
            );
            // Total over this page colomn no 4
            pageTotal = api
                .column( 4, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 4 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 5
            pageTotal = api
                .column( 5, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 5 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 6
            pageTotal = api
                .column( 6, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 6 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 7
            pageTotal = api
                .column( 7, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 7 ).footer() ).html( // Update footer
                pageTotal
            );
            // Total over this page colomn no 8
            pageTotal = api
                .column( 8, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 8 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 9
            pageTotal = api
                .column( 9, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 9 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 10
            pageTotal = api
                .column( 10, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 10 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 11
            pageTotal = api
                .column( 11, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 11 ).footer() ).html( // Update footer
                pageTotal
            );
            // Total over this page colomn no 12
            pageTotal = api
                .column( 12, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 12 ).footer() ).html( // Update footer
                pageTotal
            );

            // Total over this page colomn no 13
            pageTotal = api
                .column( 13, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 13 ).footer() ).html( // Update footer
                pageTotal
            );
            // Total over this page colomn no 14
            pageTotal = api
                .column( 14, { page: 'current'} )
                .data()
                .reduce( function (a, b) {
                    return (intVal(a) + intVal(b)).toFixed(2);
                }, 0 );
            $( api.column( 14 ).footer() ).html( // Update footer
                pageTotal
            );



        }
    	});
	}

	
});


function generatePostData() {
		var token = document.getElementsByName("token")[0].value;
		var merchants = document.getElementById("merchants").value;
		var	dateFrom = document.getElementById("dateFrom").value;
		var	acquirer = document.getElementById('acquirer').value;
		var paymentMethod = document.getElementById("paymentMethod").value;
		var mode = document.getElementById("mode").value;
		if(merchants==''){
			merchants='ALL'
		}
		if(paymentMethod==''){
			paymentMethod='ALL'
		}
		if(acquirer==''){
			acquirer = "ALL"
		}
		
		var obj = {
			merchant : merchants,
			refundRequestDate : dateFrom,
			paymentType : paymentMethod,
			acquirer : acquirer,
			mode:mode,
			token : token,
			"struts.token.name" : "token",
		}
		return obj;
	}







</script>
</head>
<body id="mainBody">
	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
<div style="overflow-x:scroll !important;" class="refundSummaryRpt txnf">
<div>
	<h2>Refund Summary Report</h2>
	<div class="clearfix">
		<div class="cust">
			<label for="acquirer">Merchant:</label><br/>
			<div class="txtnew">
				<s:select name="merchants" class="input-control"
				id="merchants" headerKey="" headerValue="ALL"
				list="merchantList" listKey="emailId"
				listValue="businessName"
				autocomplete="off" />
			</div>
		</div>
		<div class="cust">
			<label for="acquirer">Date:</label><br/>
			<div class="txtnew">
				<s:textfield type="text" id="dateFrom" name="dateFrom" class="form-control" autocomplete="off" readonly="true" />
			</div>
		</div>
		<div class="cust">
			
					<label for="account">Acquirer:</label><br />					
				<div class="txtnew">
					<s:select  name="acquirer"
		class="form-control" id="acquirer" headerKey=""
		headerValue="All"
		list="@com.pay10.commons.util.AcquirerTypeUI@values()"
		listKey="code" listValue="name" autocomplete="off"/>
				</div>								
		</div>
		<div class="cust">
			<label for="acquirer">Payment Type:</label><br/>
			<div class="txtnew">
				<s:select headerKey="" headerValue="ALL" class="form-control"
					list="@com.pay10.commons.util.PaymentType@values()"
					listValue="name" listKey="code" name="paymentMethod"
					id="paymentMethod" autocomplete="off" value="" />
			</div>
		</div>
		<div class="cust">
			<label for="acquirer">Mode:</label><br/>
			<div class="txtnew">
				<select class="form-control" id="mode">
					<option value="amount">Amount</option>
					<option value="count">Count</option>
				</select>
			</div>
		</div>
		
										
	</div>
	<div class="buttonDiv">
		<input type="button" id="submitBtn" value="Submit" class="btn btn-sm btn-success">
	</div>
	</div>

	<div class="scrollD">
		<table id="myTable" class="display" style="width:100%">
        <thead align="centre">
            <tr>
                <th>Acquirer</th>
				<th style='text-align: center'>Payment Type</th>
				<th style='text-align: center'>Mop</th>
				<th style='text-align: center'>Txn Initiated</th>
				<th style='text-align: center'>Captured</th>
				<th style='text-align: center'>Declined</th>
				<th style='text-align: center'>Rejected</th>
				<th style='text-align: center'>Pending</th>
				<th style='text-align: center'>Error</th>
				<th style='text-align: center'>Timeout</th>
				<th style='text-align: center'>Failed</th>
				<th style='text-align: center'>Invalid</th>
				<th style='text-align: center'>Acquirer down</th>
				<th style='text-align: center' >Failed At Acquirer</th>
			 	<th style='text-align: center' >Acquirer Timeout</th>
            </tr>
        </thead>
        <tfoot>
            <tr>
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
                <th></th>
                <th></th>
                <th></th>
                <th></th>
            </tr>
        </tfoot>
    </table>
				</div>

</div>  
</body>
</html>