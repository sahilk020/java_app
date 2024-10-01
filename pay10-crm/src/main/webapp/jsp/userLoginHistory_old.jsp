<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<s:if test="%{#session.USER.UserType.name()=='MERCHANT'}">
<title>User Login History</title>
</s:if>
<s:elseif test="%{#session.USER.UserType.name()=='SUBUSER'}">
<title>User Login History</title>
</s:elseif>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>

<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		handleChange();
	});
	function handleChange() {
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
				title : 'User Login History',
				exportOptions : {
					columns : [ ':visible' ]
				}
			}, {
				extend : 'pdfHtml5',
				title : 'User Login History',
				exportOptions : {
					columns : [ ':visible' ]
				}
			},{
				extend : 'print',
				title : 'User Login History',
				exportOptions : {
					columns : [ 0, 1, 2, 3, 4, 5, 6 ]
				}
			},{
				extend : 'colvis',
				columns : [ 1, 2, 3, 4, 5, 6,]
			} ],
			"ajax" : {
				type : "POST",
				url : "loginHistorySubUserAction",
				data : {
					emailId : document.getElementById("merchant").value,
					token:token,
				    "struts.token.name": "token",
				}
			},
			"bProcessing" : true,
			"bLengthChange" : true,
			"bDestroy" : true,
			"order" : [ [ 5, "desc" ] ],
			"iDisplayLength" : 10,
			"aoColumns" : [ {
				"mData" : "emailId"
			}, {
				"mData" : "ip"
			}, {
				"mData" : "browser"
			}, {
				"mData" : "os"
			}, {
				"mData" : null,
				'mRender': function (data) {
					 
				      if (data.status) {
				        return 'SUCCESS';
                   } else {
				        return 'FAILED';
                 	}
               }
			}, {
				"mData" : "timeStamp"
			}, {
				"mData" : "failureReason"
			} ]
		});
	}
</script>

<style>
.dataTables_wrapper .dataTables_filter input{
	display: none !important;
}

.dataTables_wrapper .dataTables_filter label{
	display: none !important;
}
</style>
</head>
<body>
	<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			
			<s:if test="%{#session.USER.UserType.name()=='MERCHANT'}">
			<td align="left"><h2>User Login History</h2></td>
				<td width="15%" align="left" valign="middle">
					<div class="txtnew" style="padding:0 2px 0 0">
						<s:select name="merchants" class="input-control" id="merchant"
							headerKey="ALL USERS" headerValue="ALL USERS" listKey="emailId"
							listValue="emailId" list="merchantList" autocomplete="off" onchange="handleChange();" 
							 />
					</div>
				</td>
			</s:if>
			
			<s:elseif test="%{#session.USER.UserType.name()=='SUBUSER'}">
			<td align="left"><h2>Login History</h2></td>
				<td width="15%" align="left" valign="middle">
					<div class="txtnew" style="padding:0 2px 0 0">
						<s:select name="merchants" class="input-control" id="merchant" listKey="emailId"
							listValue="emailId" list="merchantList" autocomplete="off" onchange="handleChange();"
							/>
					</div>
				</td>
			</s:elseif>
		</tr>
		<tr>
			<td align="left" colspan="2" style="padding: 10px;"><br> <br>
            <div class="scrollD">
				<table id="loginHistoryDataTable" class="display table" cellspacing="0" width="100%">
					<thead>
						<tr class="boxheadingsmall">
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