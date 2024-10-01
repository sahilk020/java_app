<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<style>

		
.heading{
   text-align: center;
    color: #496cb6;
    font-weight: bold;
    font-size: 22px;
}
.samefnew {
    width: 15.5% !important;
    float: left;
    font: bold 13px arial !important;
    color: #333;
    line-height: 22px;
    margin: 0 0 0 10px;
}
.cust {
    width: 25% !important;
    float: left;
    font: bold 13px arial !important;
    color: #333;
    line-height: 22px;
    margin: 0px 13px 20px -5px !important;
}
.submit-button{
	width:10% !important;
	height:28px !important;
	margin-top:-4px !important;
}
.MerchBx {
    min-width: 92%;
    margin: 15px;
    margin-top: 25px !important;
    padding: 0;
}

table.dataTable thead .sorting {
    background: none !important;
}
.sorting_asc {
    background:none !important;
}
table.dataTable thead .sorting_desc {
    background: none !important;
}
table.dataTable thead .sorting {
     cursor: default !important;
}
table.dataTable thead .sorting_desc, table.dataTable thead .sorting {
    cursor: default !important;
}
		

</style>

<title>Search Transaction</title>
	<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
	<link href="../css/Jquerydatatable.css" rel="stylesheet" />
	<link rel="stylesheet" href="../css/loader.css">
	<link href="../css/default.css" rel="stylesheet" type="text/css" />
	<link href="../css/jquery-ui.css" rel="stylesheet" />
	<script src="../js/jquery.js"></script>
	<script src="../js/jquery.dataTables.js"></script>
	<script src="../js/jquery-ui.js"></script>
	<script type="text/javascript" src="../js/moment.js"></script>
	<script type="text/javascript" src="../js/daterangepicker.js"></script>
	<link href="../css/loader.css" rel="stylesheet" type="text/css" />
	<script src="../js/jquery.popupoverlay.js"></script>
	<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
	<script type="text/javascript" src="../js/pdfmake.js"></script>
	<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
	<script src="../js/messi.js"></script>
	<link href="../css/messi.css" rel="stylesheet" />
	<script src="../js/commanValidate.js"></script>
	


<script type="text/javascript">

$(function() {
	
	var table = $('#searchTransactionDatatable').DataTable({
		
		
	            destroy : true,
				"searching": false,
				"paging": false,
				"info": false

	});
	
    $('#searchButton').on('click', function() {
		 var orderId = document.getElementById("orderId").value;
		 var pgRefId = document.getElementById("pgRefId").value;
		 
		 
		 var token  = document.getElementsByName("token")[0].value;
		 
        var table = $('#searchTransactionDatatable').DataTable({
			
			"searching": false,
			"info": false,
			"paging":   false,
			"destroy": true,

			
			"ajax": {
			    "url": "searchTransactionAgentAction",
			    "type": "POST",
			    "data": {
					"orderId":orderId,
				    "pgRefNum":pgRefId,
					"struts.token.name": "token",
					"token" : token,
					},						
			  },
				  "columns": [
				        { "data": "transactionId" },
					    { "data": "pgRefNum" },
						{ "data": "merchants" },
						{ "data": "customerName" },
						{ "data": "cardMask" },
			            { "data": "orderId" },
			            { "data": "createDate" },
			            { "data": "paymentMethods" },
			            { "data": "txnType" },
						{ "data": "mopType" },
			            { "data": "status" },
			            { "data": "amount" }
						
			        ]

        });
    });
});

</script>

<script>
 $(function() {
            $(':text').on('input', function() {
                if( $(':text').filter(function() { return !!this.value; }).length > 0 ) {
                     $('#searchButton').prop('disabled', false);
                } else {
                     $('#searchButton').prop('disabled', true);
                }
            });
    });
</script>

	
</head>
<body id="mainBody">

	<h2 class="heading">Search Transaction</h2>
	<br>
	<br>
	 <table class="table98 padding0">
        
        <tr>
          <td align="center">&nbsp;</td>
           
        </tr>
 
        <tr>
          <td align="center">&nbsp;</td>
          <td height="10" align="center">
    

     <table id="mainTable" width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txnf">
		
		    <tr>
			    <td colspan="5" align="left" valign="top">
				<div class="MerchBx">
					
					<div class="cust">
						Order Id:<br>
						<div class="txtnew">
							<input type="text" id="orderId" value="" class="form-control">
								</input>
						</div>
					</div>
					
					<div class="cust">
						PG Ref Id:<br>
						<div class="txtnew">
							<input type="text" id="pgRefId" value="" class="form-control" maxlength="16" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');">
								</input>
						</div>
					</div>

					<div class="samefnew-btn">
						&nbsp;<br>
						<div class="txtnew">
							<input type="button" id="searchButton" value="Submit"
								class="btn btn-sm btn-block btn-success submit-button" disabled="disabled">
								</input>
						</div>
					</div>

					
				</div>
			</td>
		</tr>
		
		 <tr>
						<td align="left" valign="top" style="padding: 10px; border-right: 1px solid #e0d2d2c4;"><br>
							<div class="scrollD">
								<table id="searchTransactionDatatable" align="center" class="display" cellspacing="0" width="100%">
									<thead>
										<tr class="boxheadingsmall" style="font-size: 11px;">
										    <th style="text-align:left;" data-orderable="false">Txn ID</th>
											<th style="text-align:left;" data-orderable="false">PG Ref No</th>
											<th style="text-align:left;" data-orderable="false">Merchant</th>
											<th style="text-align:left;" data-orderable="false">Customer Name</th>
											<th style="text-align:left;" data-orderable="false">Card Mask</th>
											<th style="text-align:left;" data-orderable="false">Order ID</th>
											<th style="text-align:left;" data-orderable="false">Date</th>
											<th style="text-align:left;" data-orderable="false">Payment Method</th>
											<th style="text-align:left;" data-orderable="false">Txn Type</th>
											<th style="text-align:left;" data-orderable="false">MOP</th>
											<th style="text-align:left;" data-orderable="false">Status</th>
											<th style="text-align:left;" data-orderable="false">Amount</th>
										</tr>
									</thead>
								
								</table>
							</div>
						</td>
					</tr>
		
		
	</table>
                
     
        </td>
    </table>
	

</body>
</html>