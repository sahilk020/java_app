<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Merchant Accounts</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>

<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<style>
	.displayNone {
		display: none;
	}
	table.dataTable.display tbody tr.odd {
		background-color: #e6e6ff !important;
	}
	table.dataTable.display tbody tr.odd > .sorting_1{
		 background-color: #e6e6ff !important;
	}
	table.display td.center{
		text-align: left !important;
	}
	.btn:focus{
			outline: 0 !important;
		}
	</style>
</head>
<body>
	<div id = "ViewtenantListDiv" style="overflow:scroll !important;">
	<table width="100%" align="left" cellpadding="0" cellspacing="0" class="formbox" >		
		<tr>
		<td align="left" style="padding:10px;" ><br /><br />
        <div class="scrollD">
	<table id="datatable" class="display" cellspacing="0" width="100%" >
		<thead>
			<tr class="boxheadingsmall">
				<th>Company Name</th>
				<th>Tenant Number</th>
				<th>Email</th>
				<th>User Name </th>
				<th>Status</th>
				<th>UserType</th>
				<th>Mobile</th>
				<th>Registration Date</th>
				<th>Edit</th>
				<th>Pay Id</th>
			</tr>
		</thead>
	</table>
    </div>
	</td></tr></table>
</div>
    <s:form name="editAdmin" action="adminSetup">
		<s:hidden name="emailId" id="email" value="" />	
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</s:form>
	<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
		renderTable();
		});
	});
	function handleChange() {
		reloadTable();
	}
	function decodeVal(text) {
		return $('<div/>').html(text).text();
	}
	function renderTable() {
			var token  = document.getElementsByName("token")[0].value;
			$('#datatable').dataTable({
				dom : 'BTftlpi',
				buttons : [ {
					extend : 'copyHtml5',
					exportOptions : {
								columns : [':visible :not(:last-child)']
							}
				}, {
					extend : 'csvHtml5',
					title : 'Merchant List',
					exportOptions : {
								columns : [':visible :not(:last-child)']
							}
				}, {
					extend : 'pdfHtml5',
					title : 'Merchant List',
					exportOptions : {
								columns : [':visible :not(:last-child)']
							}
				}, {
					extend : 'print',
					title : 'Merchant List',
					exportOptions : {
								columns : [':visible :not(:last-child)']
							}
				},{
					extend : 'colvis',
					//           collectionLayout: 'fixed two-column',
					columns : [ 1, 2, 3, 4, 5, 6,7]
				}],			
				"ajax" : {
					"url" : "adminList",
					"type" : "POST",
					"data" : generatePostData
				},
				"bProcessing" : true,
						"bLengthChange" : true,
						"bDestroy" : true,
						"iDisplayLength" : 10,
						"order" : [ [ 1, "desc" ] ],
				"aoColumns" : [ 
					{
					"mData" : "companyName"
				}, 
				{
					"mData" : "tenantNumber"
				}, 
				{
					"mData" : "emailId"
				}, 
				{
					"mData" : "businessName"
				},{
					"mData" : "status"
				},{
					"mData" : "userType"
				},	{
					"mData" : "mobile"
				},{
					"mData" : "registrationDate"
				},
				{
					"mData" : null,
					"sClass" : "center",
					"bSortable" : false,
					"mRender" : function() {
					return '<button class="btn btn-info btn-xs">Edit</button>';
											}
				},{
					"data" : null,
					"visible" : false,
					"className" : "displayNone",
					"mRender" : function(row) {
			              return "\u0027" + row.payId;
			       }
				} ]
			});
			
			$(function() {
				var table = $('#datatable').DataTable();
					$('#datatable tbody').on('click','td',function(){
						var rows = table.rows();
						var columnVisible = table.cell(this).index().columnVisible;
						var rowIndex = table.cell(this).index().row;
						
						var email =	table.cell(rowIndex, 2).data();
						document.getElementById('email').value = decodeVal(email);
						if (columnVisible == 8) {
							document.getElementById('email').value = decodeVal(email);
								document.editAdmin.submit();
							}	
					    
				});
			});
		}
		function reloadTable() {
			var tableObj = $('#datatable');
			var table = tableObj.DataTable();
			table.ajax.reload();
		}
		function generatePostData() {
			var token = document.getElementsByName("token")[0].value;
			var obj = {				
					token : token,
			};
			return obj;
		}
	</script>
 </body>
</html>