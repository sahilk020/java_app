<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>


<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Merchant Segment</title>







	<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />

		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>

<%
	DecimalFormat d = new DecimalFormat("0.00");
	Date d1 = new Date();
	SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY");
	String currentDate = df.format(d1);
%>


<style type="text/css">
button#btnCancel {
    padding: calc(0.825rem + 1px) calc(1.75rem + 1px);
}


</style>

<script type="text/javascript">
	function callMe(email, status, payId, segment) {
		debugger
		if (status == 'ACTIVE') {
			$("#payId").val(payId);
			$("#email").val(email);
			$("#segmentType").val(segment);
			$('#editModal').modal('show');
		} else {

		}

	}
</script>
<script type="text/javascript">  
function cancelbutton(){
	$(function () {
        $("#btnCancel").click(function () {
            $("#editModal").modal("hide");
        });
    });
}
</script>
<script type="text/javascript">
	function save() {
		debugger
		
			var payid = $("#payId").val();
			var email = $("#email").val();
			var segmentName = $("#segments").val();
			var segmentType = $("#segmentType").val();
			var swapingFlag = true;
			 
			//alert(payid + "\n" + email + "\n" + segmentName + "\n" +segmentType); 

			if(segmentName == "ALL"){
				alert("Please select only Segment Type");
				swapingFlag = false;
			} 
			if (segmentName==segmentType){
				alert("merchant belong to same segment, Please choose other one!");
				swapingFlag = false;
			} 
			
			if(swapingFlag) {
				
				var urls = new URL(window.location.href);
				var domain = urls.origin;

				$.ajax({
					url : domain + "/crmws/segment/updateSegment",
					type : "POST",
					headers : {
						'Accept' : 'Application/json',
						'Content-Type' : 'application/json'
					},
					data : JSON.stringify({
						payId : payid,
						emailId : email,
						segmentName : segmentName
					}),

					success : function(resData) {
						//var res = resData;
						//alert();
						//alert(resData);
						alert("Merchant Updated Successfully !!");
						//alert("Hi");
						//alert(resData.message);
						setTimeout(function(){// wait for 5 secs(2)
					           location.reload(); // then reload the page.(3)
					      }, 1000); 

					},
					error : function(data) {
						//alert(data);
						alert("Merchant Updated Successfully !!");
						setTimeout(function(){// wait for 5 secs(2)
					           location.reload(); // then reload the page.(3)
					      }, 1000); 
					}
				});
			}
			
			
		
	}
</script>

	<script type="text/javascript"> 
//     $(document).ready(function() {     
        
//         $('#datatable').DataTable( {
//         	dom: "<'datatableHeader'<'row'<'col-md-6 col-sm-12'f><'col-md-6 col-sm-12'B>>>" +
//         	"<'row'<'col-sm-12'<'table-responsive'tr>>>"+
//      		"<'datatableFooter' <'row'<'col-md-6 col-sm-12'i> <'col-md-6 col-sm-12'p> >>",
//             autoWidth: false,
//             "order": [[ 0, "desc" ]],
//             buttons: [
//              {
             
//                 extend: 'copy',
//                 text: 'COPY',
<%--                 title:'Merchant Segment - ' + '<%=currentDate%>', --%>
<%--                 message:'<%=currentDate%>', --%>
//             },  {
             
//                 extend: 'csv',
//                 text: 'CSV',
<%--                 title:'Merchant Segment - ' + '<%=currentDate%>', --%>
              
//             },{
             
//                 extend: 'excel',
//                 text: 'EXCEL',
<%--                 title:'Merchant Segment - ' + '<%=currentDate%>', --%>
            
//             }, {
             
//                 extend: 'pdf',
//                 text: 'PDF',
//                 title:'Merchant Segment Report',
<%--                 message:"Generated on" + "<%=currentDate%>" + "", --%>
             
              
//             },  {
             
//                 extend: 'print',
//                 text: 'PRINT',
<%--                 title:'Merchant Segment - ' + '<%=currentDate%>', --%>
              
//             },{
//                 extend: 'colvis',
//                 columnText: function ( dt, idx, title ) 
//                 {
//                     return (idx+1)+': '+title;
//                 }
//             }
//             ]
//         } ); 
//         } ); 

$(document).ready(function() {
	
	$('#example').DataTable({
		dom : 'BTftlpi',
		paging:'true',
		scrollX:true,
		scrollY:true,
		buttons : [ {

			extend : 'copy',
			text : 'COPY',
			title : 'Transaction Report',
			exportOptions : {
				columns: [':visible :not(:last-child)']
			},

		}, {

			extend : 'csv',
			text : 'CSV',
			title : 'Transaction Report',
			exportOptions : {
				columns: [':visible :not(:last-child)']
			},

		}, {

			extend : 'pdf',
			text : 'PDF',
			title : 'Transaction Report',
			exportOptions : {
				columns: [':visible :not(:last-child)']
			},
		}, {

			extend : 'print',
			text : 'PRINT',
			title : 'Transaction Report',
			exportOptions : {
				columns : ':visible'
			},

		}, {
			extend : 'colvis',
			 columnText : function(dt, idx, title) {
				return (idx + 1) + ': ' + title;
				
			} 
		
		} ]



	
	});
});
function myFunction() {
	var x = document.getElementById("actions11").value;
	if (x == 'csv') {
		document.querySelector('.buttons-csv').click();
	}
	if (x == 'copy') {
		document.querySelector('.buttons-copy').click();
	}
	if (x == 'pdf') {
		document.querySelector('.buttons-pdf').click();
	}

	// document.querySelector('.buttons-excel').click();
	// document.querySelector('.buttons-print').click();


}
</script>
 
<style type="text/css">
.dt-buttons {
    display: none;
}
</style>
</head>
<body id="kt_body" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed" style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
	<div class="modal" id="editModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center" style="font-weight: bold;">
					Segment Mapping</div>
				<div class="modal-body" style="word-break: break-word;">
					<div class="row">
						<div class="col-sm-6">
							<label for="merchant">Pay Id</label> <br /> <input type="text"
								name="payId" id="payId" readonly="readonly">
						</div>

						<div class="col-sm-6">
							<input type="hidden" id="email" name="email">
							<input type="hidden" id="segmentType" name="segmentType">
							<label for="merchant">Segment Type</label> <br />
							<s:select headerKey="ALL" headerValue="ALL" name="segmentName"
								id="segments" listKey="segment" class="form-select form-select-solid"
								list="segments" listValue="segment" />

						</div>

					</div>

					<input type="hidden" id="diffId" /> <input type="hidden"
						id="payload" />

					<div id="innerDiv"
						style="background-color: lightgrey; text-align: center;"></div>
				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-primary btnSave1" id="btnSave"
						data-dismiss="modal" onclick="save();">Save</button>
					<button type="button" class="btn btn-primary btn-danger" id="btnCancel"
						data-dismiss="modal" onclick="cancelbutton()">Close</button>
				</div>
			</div>
		</div>
	</div>	
	
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<!--begin::Toolbar-->
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Merchant Segment List</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">
											<a href="home" class="text-muted text-hover-primary">Dashboard</a>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
                                        <!--begin::Item-->
										<li class="breadcrumb-item text-muted">Manage Users</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Merchant Segment List</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>
						<!--end::Toolbar-->
						<!--begin::Post-->
						<div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">								
                                <div class="row my-5">
                                    <div class="col">
                                      <div class="card">
                                        <div class="card-body">
                                         <div class="row g-9 mb-8">
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                            <s:form name="merchant" action="merchantSegment">
                                            <label for="merchant" class="d-flex align-items-center fs-6 fw-semibold mb-2">Segment Type</label> <br />
				<s:select headerKey="ALL" headerValue="ALL" name="segmentName"
					id="segments" listKey="segment" class="form-select form-select-solid segmentsearch"
					list="segments" listValue="segment" onchange="changeSegment();" />
					<s:hidden name="payId" id="hidden" value="" />
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		<s:submit value="Submit" id="sbtbtn" style="display:none;"></s:submit>
	</s:form>
                                            </div>
                                            </div>
                                          <!--begin::Input group-->
                                         <%--  <div class="row g-9 mb-8 justify-content-end">
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Actions"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                                <option value="">Actions</option>
                                                <option value="copy">Copy</option>
                                                <option value="csv" >CSV</option>
                                                <option value="pdf">PDF</option>
                                                <option value="print">PRINT</option>
                                              </select>
                                            </div>
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Customize Columns"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                               <option value="">Customize Columns</option>
                                                <option value="payId">Pay Id</option>
                                                <option value="businessName">Business Name</option>
                                                <option value="emailId">Email</option>
                                                <option value="userStatus">Status</option>
                                                 <option value="mobile">Mobile</option>
                                                <option value="registrationDate">Registration Date</option>
                                               
                                                <option value="">Action</option>
                                           
                                                                                                
                                              </select>
                                            </div>
                                          </div> --%>
                                          
                                          <div class="row g-9 mb-8 justify-content-end">
											<div class="col-lg-2 col-sm-12 col-md-6">
												<select name="currency" data-control="select2"
													data-placeholder="Actions" id="actions11"
													class="form-select form-select-solid actions dropbtn1"
													data-hide-search="true" onchange="myFunction();">
													<option value="">Actions</option>
													<option value="copy">Copy</option>
													<option value="csv">CSV</option>
													<option value="pdf">PDF</option>
												</select>
											</div>
											<div class="col-lg-4 col-sm-12 col-md-6">
												<div class="dropdown1">
													<button
														class="form-select form-select-solid actions dropbtn1">Customize
														Columns</button>
													<div class="dropdown-content1">
														<a class="toggle-vis" data-column="0">Pay Id</a>
														<a class="toggle-vis" data-column="1">Business Name</a>
														<a class="toggle-vis" data-column="2">Email</a>
														 <a class="toggle-vis" data-column="3">Status</a>
														<!--<a class="toggle-vis" data-column="4">User Type</a> -->
														<a class="toggle-vis" data-column="5">Mobile</a>
														<a class="toggle-vis" data-column="6">Registration Date</a>
													</div>
												</div>
											</div>
										</div>                        
                                          
                                          
                                       <div class="row g-9 mb-8">
                                          <div class="dataTables_wrapper dt-bootstrap4 no-footer">
                                         <table id="example" class="table table-striped table-row-bordered gy-5 gs-7">
                                           <thead>
                                                <tr class="fw-bold fs-6 text-gray-800">
                                                <th>Pay Id</th>
												<th>Business Name</th>
												<th>Email</th>
												<th>Status</th>
												<th>Mobile</th>
												<th>Registration Date</th>					
												<th>Action</th>
						
	
                                                </tr>
                                              </thead>
                                             			<tbody>
					<s:iterator value="segmentInfo" var="x">
						<tr class="odd" valign="top" colspan="15">
							<td align="left" bgcolor="#f2f2f2"><s:property value="payId" /></td>
							<td align="left" bgcolor="#f2f2f2"><s:property
									value="businessName" /></td>
							<td align="left" bgcolor="#f2f2f2"><s:property
									value="emailId" /></td>
							<td align="left" bgcolor="#f2f2f2"><s:property
									value="userStatus" /></td>
							<td align="left" bgcolor="#f2f2f2"><s:property
									value="mobile" /></td>
							<td align="left" bgcolor="#f2f2f2"><s:property
									value="registrationDate" /></td>
							<s:set var="userId" value="userStatus" />
							<td align="left" bgcolor="#f2f2f2">
								<!-- <input type="button" value='Edit' data-toggle="modal" data-target="#editModal" class="btn btn-lg btn-success" /> -->
								<button class="btn btn-primary btn-lg" name="editBtn" disabled="disabled"
								onclick="callMe('<s:property value="emailId"/>','<s:property value="userStatus"/>','<s:property value="payId"/>','<s:property value="segment"/>');">Edit</button>
							</td>
						</tr>
					</s:iterator>
				</tbody>
             </table>
            <input type="hidden" name="my" id="myText" value="">
           </div>
          </div>
        </div>
                                 
                                      
                                      </div>
                                    </div>
                                     </div>
                                    	<div id="Modal" class="modal fade" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Modal Header</h4>
				</div>
				<div class="modal-body">
					<p>Some text in the modal.</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>

		</div>
	</div>
                               
							</div>
							<!--end::Container-->
						</div>
							<!--end::Container-->
						</div>
						<!--end::Post-->
	
		
	

<script type="text/javascript">

	function changeSegment(){
		//alert("Hiii");
		$("#sbtbtn").click();
	}
	
</script>


<script type="text/javascript">
			$('a.toggle-vis').on('click', function (e) {
				debugger
				e.preventDefault();
				table = $('#example').DataTable();
				// Get the column API object
				var column1 = table.column($(this).attr('data-column'));
				// Toggle the visibility
				column1.visible(!column1.visible());
				if ($(this)[0].classList[1] == 'activecustom') {
					$(this).removeClass('activecustom');
				}
				else {
					$(this).addClass('activecustom');
				}
			});

		</script>


<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>

<!-- added by deep user management -->
<script>
		$('#example').on( 'draw.dt', function () {
			enableBaseOnAccess();
		} );
		function enableBaseOnAccess() {
			setTimeout(function(){
				if ($('#merchantSegment').hasClass("active")) {
					var menuAccess = document.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["merchantSegment"];
					if (access.includes("Update")) {
						var edit = document.getElementsByName("editBtn");
						for (var i = 0; i < edit.length; i++) {
							var edit1 = edit[i];
							edit1.disabled=false;
						}
					}
				}
			},500);
	}
	</script>
	<!-- search -->

	
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	 <script type="text/javascript">
	$(document).ready(function () {
	$(".segmentsearch").select2();
		});
</script> 
</script>
</body>
</html>