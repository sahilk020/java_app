<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Menu List</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">

</head>
<body>
	<div>
		<div cssClass="indent">
			<s:form action="addMenuDetailsAction" method="post">
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
				<table width="100%" border="0" cellspacing="0" cellpadding="7"
					class="formboxRR">
					<tr>
						<td align="left" valign="top">
							<div class="addfildn">
								<div class="rkb">
									<div class="addfildn">
										<div class="fl_wrap">
											<label class='fl_label'
												style="padding: 0; font-size: 13px; font-weight: 600;">Menu
												Name</label>
											<s:textfield id="payId"
												style="margin-top:10px;    font-weight:500; font-size:14px;"
												name="menuName" type="text"></s:textfield>
										</div>
									</div>

									<div class="addfildn">
										<div class="fl_wrap">
											<label class='fl_label'
												style="padding: 0; font-size: 13px; font-weight: 600;">Description</label>
											<div class="txtnew">
												<s:textfield id="description"
													style="margin-top:10px; font-weight:normal; font-size:14px;"
													name="description" type="text"></s:textfield>
											</div>
										</div>
									</div>

									<div class="addfildn">
										<div class="fl_wrap">
											<label class='fl_label'
												style="padding: 0; font-size: 13px; font-weight: 600;">Is
												Active?</label>
											<s:checkbox name="isActive" id="isActive" />
										</div>
									</div>
									<div class="addfildn">
										<div class="fl_wrap">
											<s:submit method="submit" style="margin-right: 10px;"
												class="btn btn-success btn-md" value="Save">
											</s:submit>
										</div>
									</div>
								</div>
							</div>
						</td>
					</tr>

				</table>
			</s:form>
		</div>
	</div>
	<h2 class="pageHeading">Menu List</h2>
	<div style="overflow: scroll !important;">
		<table width="100%" align="left" cellpadding="0" cellspacing="0"
			class="formbox">
			<tr>
				<td align="left" style="padding: 10px;"><br /> <br />
					<div class="scrollD">
						<table id="datatable" class="display" cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall">
									<th>Sr no</th>
									<th>Menu name</th>
									<th>Menu Description</th>
									<th>Active</th>
									<th>Edit</th>
									<th>Delete</th>
									<th>Id</th>
								</tr>
							</thead>
						</table>
					</div></td>
			</tr>
		</table>
	</div>
	<s:form name="menu" action="editMenuDetailsAction">
		<s:hidden name="id" id="id" value=""></s:hidden>
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</s:form>
	<s:form name="deleteMenu" action="deleteMenuDetailsAction">
		<s:hidden name="id" id="deleteId" value=""></s:hidden>
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
			var buttonCommon = {
		        exportOptions: {
		            format: {
		                body: function ( data, column, row, node ) {
		                    return data;
		                }
		            }
		        }
		    };
			$('#datatable').dataTable({
				dom : 'BTftlpi',
				
				'columnDefs':[{ 
                    'searchable':false, 
                    'targets' :[2] 
                }],

				buttons : [
				$.extend( true, {}, buttonCommon, {
					extend : 'copyHtml5',
					exportOptions : {
						columns: [':visible :not(:last-child)']
					}
				} ),
				$.extend( true, {}, buttonCommon, {
					extend : 'csvHtml5',
					exportOptions : {
						columns: [':visible :not(:last-child)']
					}
				} ),
				{
					extend : 'pdfHtml5',
					title : 'Menu List',
					orientation: 'landscape',
					exportOptions : {
						columns: [':visible :not(:last-child)']
					},
					customize: function (doc) {
					    doc.defaultStyle.alignment = 'center';
     					doc.styles.tableHeader.alignment = 'center';
					  }
				}, 
				// Disabled print button.
				/* {extend : 'print',title : 'Merchant List',exportOptions : {columns: [':visible :not(:last-child)']}}, */
				{
					extend : 'colvis',
					//collectionLayout: 'fixed two-column',
					columns : [0, 1, 2]
				}],			
				"ajax" : {
					"url" : "menuDetailsAction",
					"type" : "POST",
					"data" : generatePostData
				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bAutoWidth" : false,
				"iDisplayLength" : 10,
				"order": [[ 1, "asc" ]],
				"aoColumns" : [ {
					"mData" : "id"
				},{
					"mData" : "menuName"
				}, {
					"mData" : "description"
				}, {
					"mData" : "active"
				}, {
					"mData" : null,
					"sClass" : "center",
					"bSortable" : false,
					"mRender" : function() {
						return '<button class="btn btn-info btn-xs" onclick="ajaxindicatorstart1()">Edit</button>';
					}
				}, {
						"mData" : null,
						"sClass" : "center",
						"bSortable" : false,
						"mRender" : function() {
							return '<button class="btn btn-danger acquirerRemoveBtn" onclick="ajaxindicatorstart1()">Delete</button>';
						}
					},
					{
						"data" : null,
						"visible" : false,
						"className" : "displayNone",
						"mRender" : function(row) {
				              return row.id;
						}
				} ]
			});
			$(document).ready(function() {
				var table = $('#datatable').DataTable();
					$('#datatable tbody').on('click','td',function(){
						var rows = table.rows();
						var columnVisible = table.cell(this).index().columnVisible;
						var rowIndex = table.cell(this).index().row;
						var id = table.cell(rowIndex, 0).data();
						document.getElementById('id').value = id;
						document.getElementById('deleteId').value = id;
						if (columnVisible == 4) {
						    document.menu.submit();
						}
						if (columnVisible == 5) {
						    document.deleteMenu.submit();
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