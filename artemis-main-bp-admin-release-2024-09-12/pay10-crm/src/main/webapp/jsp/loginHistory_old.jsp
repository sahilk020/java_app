<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login History</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<script type="text/javascript" src="../js/moment.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>

<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  //$("#merchant").select2();
});
</script>

<style>
.displayNone {
	display: none;
}
.dataTables_wrapper .dataTables_filter input{
	display: none !important;
}
.dataTables_wrapper .dataTables_filter label{
	display: none !important;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
}
</style>

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
			$('#loginHistoryDataTable').dataTable({
				dom : 'BTftlpi',
				buttons : [ {
					extend : 'copyHtml5',
					exportOptions : {
						columns : [ ':visible' ]
					}
				}, {
					extend : 'csvHtml5',
					title : 'Login History',
					exportOptions : {
						columns : [ ':visible' ]
					}
				}, {
					extend : 'pdfHtml5',
					orientation: 'landscape',
					title : 'Login History',
					pageSize: 'LEGAL',
					exportOptions : {
						columns : [ ':visible' ]
					},
					customize: function (doc) {
					    doc.content[1].table.widths = Array(doc.content[1].table.body[0].length + 1).join('*').split('');
					    doc.defaultStyle.alignment = 'center';
     					doc.styles.tableHeader.alignment = 'center';
					  }
				}, {
					extend : 'print',
					title : 'Login History',
					exportOptions : {
						columns : [ ':visible']
					}
				},{
					extend : 'colvis',
					//           collectionLayout: 'fixed two-column',
					columns : [0, 1, 2, 3, 4, 5, 6]
				} ],
				"ajax" : {
					"url" : "loginHistoryAction",
					"type" : "POST",
					data : function(d) {
						return generatePostData(d);
					}
				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bDestroy" : true,
			        "serverSide": true,
			        "paginationType": "full_numbers", 
			        "lengthMenu": [10, 25, 50, 100],
			        "order" : [ [ 1, "desc" ] ], 
				"columns" : [
				{
					"data" : "businessName",
					
				}, {
					"data" : "emailId",
					
				},{
					"data" : "ip"
			
				}, {
					"data" : "browser"
					
				}, {
					"data" : "os"
				
				},{
					"data" : "status",
				
				}, {
					"data" : "timeStamp",
				}, {
					"data" : "failureReason"
				}]
			});
		}
		function reloadTable() {
			var tableObj = $('#loginHistoryDataTable');
			var table = tableObj.DataTable();
			table.ajax.reload();
		}
		function generatePostData(a) {
			var token = document.getElementsByName("token")[0].value;
			var merchantEmailId = document.getElementById("merchant").value;
			var obj = {
			    emailId : merchantEmailId,
				draw :   a.draw,
				length : a.length,
				start : a.start,
				token : token,
				"struts.token.name" : "token",
			}
			return obj;
		}
</script>
</head>
<body>
	<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2 style="margin-left: 25% !important;">Login History</h2></td>
			<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
			<td width="15%" align="left" valign="middle" style="border-right: 1px solid #eaeaea;"><div class="txtnew" style="padding:0 8px 0 0">
					<s:select name="merchants" class="input-control showMerchant" id="merchant"
						headerKey="ALL MERCHANTS" headerValue="ALL MERCHANTS" listKey="emailId"
						listValue="businessName" list="merchantList" autocomplete="off" onchange="handleChange();" />
				</div></td>
				</s:if>
				<s:elseif test="%{#session.USER.UserType.name()=='MERCHANT'}">
				<td width="15%" align="left" valign="middle"><div class="txtnew" style="padding:0 8px 0 0">
				
				 <s:hidden id="merchant" name="merchants" value="ALL MERCHANTS"  />
			<%-- 	<s:select name="merchants" class="form-control" id="merchant"
						 listKey="emailId" style="width: 100% !important;"
						listValue="businessName" type="hidden" list="merchantList" autocomplete="off" onchange="handleChange();" /> --%>
				</div></td>
				</s:elseif>
				<s:elseif test="%{#session.USER.UserType.name()=='SUBUSER'}">
				<td width="15%" align="left" valign="middle"><div class="txtnew" style="padding:0 8px 0 0">
				 <s:hidden id="merchant" name="merchants" value="ALL MERCHANTS"  />
					<%-- <s:select name="merchants" class="form-control" id="merchant"
						 listKey="emailId"
						listValue="businessName" list="merchantList" autocomplete="off" onchange="handleChange();" /> --%>
				</div></td>
				</s:elseif>
					<s:elseif test="%{#session.USER.UserType.name()=='RESELLER'}">
				<td width="15%" align="left" valign="middle"><div class="txtnew" style="padding:0 8px 0 0">
				 <s:hidden id="merchant" name="merchants" value="ALL MERCHANTS"  />
				</div></td>
				</s:elseif>
				<s:elseif test="%{#session.USER.UserType.name()=='SUBADMIN'}">
				<td width="15%" align="left" valign="middle"><div class="txtnew" style="padding:0 8px 0 0">
				 <s:hidden id="merchant" name="merchants" value="ALL MERCHANTS"  />
				<%-- 	<s:select name="merchants" class="form-control" id="merchant"
						 listKey="emailId"
						listValue="businessName" list="merchantList" autocomplete="off" onchange="handleChange();" /> --%>
				</div></td>
				</s:elseif>
				<s:elseif test="%{#session.USER.UserType.name()=='AGENT'}">
				<td width="15%" align="left" valign="middle"><div class="txtnew" style="padding:0 8px 0 0">
				 <s:hidden id="merchant" name="merchants" value="ALL MERCHANTS"  />
				<%-- 	<s:select name="merchants" class="form-control" id="merchant"
						 listKey="emailId"
						listValue="businessName" list="merchantList" autocomplete="off" onchange="handleChange();" /> --%>
				</div></td>
				</s:elseif>
									
		</tr>
		<tr>
			<td align="left" colspan="3" style="padding: 10px;"><br> <br>
            <div class="scrollD">
				<table id="loginHistoryDataTable" class="display" cellspacing="0" width="100%">
					<thead>
						<tr class="boxheadingsmall">
							<th>Business Name</th>
							<th>Email Id</th>
							<th>IP address</th>
							<th>Browser</th>
							<th>OS</th>
							<th>Status</th>
							<th>Date</th>
							<th>Failed Login Reason</th>
						</tr>
					</thead>
				</table>
                </div>
                </td>
		</tr>
	</table>
</body>
</html>