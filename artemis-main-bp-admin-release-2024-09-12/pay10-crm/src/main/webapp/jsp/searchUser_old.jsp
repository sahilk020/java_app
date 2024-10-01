<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search User</title>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<!-- <script src="../js/jquery.min.js"></script> -->

<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>

<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<style>
.displayNone {
	display: none;
}
.dataTables_wrapper .dataTables_filter input{
	width: 50% !important;
}
</style>
<script type="text/javascript">
function decodeVal(text){	
	  return $('<div/>').html(text).text();
	}
$(document).ready(
		function() {				
			populateDataTable();
			enableBaseOnAccess();
			$("#submit").click(
					function(env) {
						/* var table = $('#authorizeDataTable')
								.DataTable(); */
						$('#searchUserDataTable').empty();
						

						populateDataTable();

					});
		});	
					
function populateDataTable() {		
	var token  = document.getElementsByName("token")[0].value;
	$('#searchUserDataTable')
			.DataTable(
					{
						dom : 'BTftlpi',
						buttons : [ {
							extend : 'copyHtml5',
							exportOptions : {
								columns : [ ':visible' ]
							}
						}, {
							extend : 'csvHtml5',
							title : 'Search User',
							exportOptions : {
								columns : [ ':visible' ]
							}
						}, {
							extend : 'pdfHtml5',
							title : 'Search User',
							exportOptions : {
								columns : [ ':visible' ]
							}
						}, {
							extend : 'print',
							title : 'Search User',
							exportOptions : {
								columns : [ 0, 1, 2, 3, 4, 5 ]
							}
						},{
							extend : 'colvis',
							//           collectionLayout: 'fixed two-column',
							columns : [ 1, 2, 3, 4, 5]
						}],
						"ajax" : {
							"url" : "searchUserAction",
							"type" : "POST",
							"data" : {
								/* emailId : document
									.getElementById("emailId").value,
								phoneNo : document
									.getElementById("phoneNo").value */
									token:token,
								    "struts.token.name": "token",
									}
						},
						"bProcessing" : true,
						"bLengthChange" : true,
						"bDestroy" : true,
						"iDisplayLength" : 10,
						"order" : [ [ 1, "desc" ] ],
						"aoColumns" : [										
										{
											"mData" : "emailId",
											"sWidth" : '25%',
										},
										{
											"mData" : "firstName",
											"sWidth" : '20%'
										},
										{
											"mData" : "lastName",
											"sWidth" : '20%'
										},
										{
											"mData" : "mobile",
											"sWidth" : '20%'
										},
										{
											"mData" : "isActive",
											"sWidth" : '10%'
										},	
										{
											"mData" : null,
											"sClass" : "center",
											"bSortable" : false,
											"mRender" : function() {
												return '<button class="btn btn-info btn-xs" disabled="disabled" id="editSubUser" name="editSubUser">Edit</button>';
											}
										},
										{
											"mData" : "payId",
											"sWidth" : '25%',
											"visible" : false,
										}]
					});

	 $(function() {

		var table = $('#searchUserDataTable').DataTable();
		$('#searchUserDataTable tbody')
				.on(
						'click',
						'td',
						function() {

							var columnVisible = table.cell(this).index().columnVisible;
							var rowIndex = table.cell(this).index().row;
							var row = table.row(rowIndex).data();

							var emailAddress = table.cell(rowIndex, 0).data();
							var firstName = table.cell(rowIndex, 1).data();
							var lastName = table.cell(rowIndex, 2).data();
							var mobile = table.cell(rowIndex, 3).data();
							var isActive = table.cell(rowIndex, 4).data();					

							if (columnVisible == 5) {
								document.getElementById('emailAddress').value = decodeVal(emailAddress);
								document.getElementById('firstName').value = firstName;
								document.getElementById('lastName').value = lastName;
								document.getElementById('mobile').value = mobile;
								document.getElementById('isActive').value = isActive;
								if ($("#editSubUser").attr("disabled") == undefined && $("#editSubUser").id != undefined){
									document.userDetails.submit();
								}
							}							
						});
	});
}
</script>
<script type="text/javascript">
	function MM_openBrWindow(theURL, winName, features) { //v2.0
		window.open(theURL, winName, features);
	}

	function displayPopup() {
		document.getElementById('light3').style.display = 'block';
		document.getElementById('fade3').style.display = 'block';
	}
</script>

</head>
<body>
	<table width="100%" align="left" cellpadding="0" cellspacing="0"
		class="txnf">
		<tr>
			<td align="left"><s:actionmessage /></td>
		</tr>
		<tr>
			<td align="left"><h2>User List</h2></td>
		</tr>
		<tr>
			<td align="left"><table width="100%" border="0" align="center"
					cellpadding="0" cellspacing="0">
					<tr>
						<td colspan="5" align="left" valign="top">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5" align="center" valign="top"><table
								width="100%" border="0" cellpadding="0" cellspacing="0">
							</table></td>
					</tr>
				</table></td>
		</tr>
		<tr>
			<td align="left" style="padding: 10px;">
            <div class="scrollD">
            <table width="100%"
					border="0" cellpadding="0" cellspacing="0" id="searchUserDataTable"
					class="display">
					<thead>
						<tr class="boxheadingsmall">			
							<th>Email</th>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Phone</th>
							<th>Is Active</th>
							<th>Action</th>
							<th></th>				
						</tr>
					</thead>
				</table>
                </div></td>
		</tr>
	</table>
	<s:form name="userDetails" action="editUserCallAction">	
		<s:hidden name="emailId" id="emailAddress" value="" />
		<s:hidden name="firstName" id="firstName" value="" />
		<s:hidden name="lastName" id="lastName" value="" />
		<s:hidden name="mobile" id="mobile" value="" />
		<s:hidden name="isActive" id="isActive" value="" />
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>	
	</s:form>
<script type="text/javascript">
$('#searchUserDataTable').on( 'draw.dt', function () {
	enableBaseOnAccess();
} );
function enableBaseOnAccess() {
		setTimeout(function(){
			if ($('#searchUser').hasClass("active")) {
				var menuAccess = document.getElementById("menuAccessByROLE").value;
				var accessMap = JSON.parse(menuAccess);
				var access = accessMap["searchUser"];
				var edits = document.getElementsByName("editSubUser");
				if (access.includes("Update")) {
					for (var i = 0; i < edits.length; i++) {
						var edit = edits[i];
						edit.disabled=false;
					}
				} else {
					for (var i = 0; i < edits.length; i++) {
						var edit = edits[i];
						edit.remove();
					}
				}
			}
		},500);
}
</script>
</body>
</html>