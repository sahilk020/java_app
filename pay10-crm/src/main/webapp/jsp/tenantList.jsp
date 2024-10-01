<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Tenant list</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
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


		
	
</head>
<body>
	<div id = "tenantListDiv" style="overflow:scroll !important;">
		<table id="mainTable" width="100%" border="0" align="center"
			cellpadding="0" cellspacing="0" class="txnf">
			
			<tr>
				<td colspan="5" align="left"><h2>&nbsp;</h2></td>
			</tr>
			<tr>
				<td align="left" style="padding: 10px;">
	<table id="datatable" class="display" cellspacing="0" width="100%" >
		<thead>
			<tr class="boxheadingsmall">
				<th>Tenant Id</th>
				<th>Company Name</th>
				<th>Mobile</th>
				<th>Email</th>
				<th>Pg Url</th>
				<th>Status</th>
				<th>Action</th>
			</tr>
		</thead>
		
	</table>
	</td>
	</tr>
	</table>
	</div>
	
    <s:form name="editTenant" action="editTenantDetails">
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
						columns : [ ':visible' ]
					}
				}, {
					extend : 'csvHtml5',
					title : 'Merchant List',
					exportOptions : {
						columns : [ 0, 1, 2, 3, 4]
					}
				}, {
					extend : 'pdfHtml5',
					title : 'Merchant List',
					exportOptions : {
						columns : [ ':visible' ]
					}
				}, {
					extend : 'print',
					title : 'Merchant List',
					exportOptions : {
						columns : [ 0, 1, 2, 3, 4]
					}
				},{
					extend : 'colvis',
					//           collectionLayout: 'fixed two-column',
					exportOptions : {
						columns : [ 0, 1, 2, 3, 4]
					}
				}],			
				"ajax" : {
					"url" : "getTenantList",
					"type" : "POST",
					"data" : generatePostData
				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bAutoWidth" : false,
				"iDisplayLength" : 10,
				"order": [[ 5, "desc" ]],
				"aoColumns" : [ 
					{
					"mData" : "tenantId"
					}, 
					{
					"mData" : "tenantCompanyName"
			     	}, 
					{
					"mData" : "tenantMobile"
			     	},
				    {
					"mData" : "tenantEmailId"
					 },
					 {
					"mData" : "tenantPgUrl"
					 },
					 {
					"mData" : "tenantStatus"
					 },
					 {
					"mData" : null,
					"mRender" : function() {	
												return '<button value="Edit" class="btn btn-info btn-xs">EDIT</button>';
										}
			     	},
				 ]
			});
			
			$(function() {
				var table = $('#datatable').DataTable();
					$('#datatable tbody').on('click','td',function(){
						var rows = table.rows();
						var columnVisible = table.cell(this).index().columnVisible;
						var rowIndex = table.cell(this).index().row;
						
						var email = table.cell(rowIndex, 3).data();
						if (columnVisible == 6) {
							document.getElementById('email').value = decodeVal(email);
					    document.editTenant.submit();
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