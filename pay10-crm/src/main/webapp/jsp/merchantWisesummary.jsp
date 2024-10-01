<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Summary Report</title>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../js/jquery.min.js" type="text/javascript"></script>
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>


<script type="text/javascript">
	$(document).ready(function() {
		//$("#merchant").select2();
	});
	$(document).ready(function() {

		// Initialize select2
		//$(".adminMerchants").select2();
	});
</script>

<script type="text/javascript">
$(document).ready(function() {
    $('#example').DataTable( {
        dom: 'BTftlpi',
        buttons: [
        	{
                
                extend: 'copy',
                text: 'COPY',
                title:'Summary Report',
				exportOptions : {
					columns : ':visible'
				},
                
            },  {
             
                extend: 'csv',
                text: 'CSV',
                title:'Summary Report',
                exportOptions : {
					columns : ':visible'
				},
            },{
             
                extend: 'pdf',
                text: 'PDF',
                title:'Summary Report',
                exportOptions : {
					columns : ':visible'
				},
               
            },  {
             
                extend: 'print',
                text: 'PRINT',
                title:'Summary Report',
                exportOptions : {
					columns : ':visible'
				},
            },{
                extend: 'colvis',
                columnText: function ( dt, idx, title ) 
                {
                    return (idx+1)+': '+title;
                }
            }
        ]
    } );
} );
</script>
<script type="text/javascript">
	function summitdetail() {
		debugger
		
// 		var res=reloadTable();
// 		if(res!=='false'){
			var datecheck=document.getElementById("dateFrom").value;
			if(datecheck!=''){
			
			document.getElementById("merchantWiseReportt").submit();
			
			}else{
				alert("Please select Date");
			}
// 		}


	}
</script>

</head>
<body id="mainBody">

<!-- 	<div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div>
 -->
	<!--begin::Toolbar-->
	<div class="toolbar" id="kt_toolbar">
		<!--begin::Container-->
		<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
			<!--begin::Page title-->
			<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
				<!--begin::Title-->
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Summary Report</h1>
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
					<li class="breadcrumb-item text-muted">Payout Generate</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item">
						<span class="bullet bg-gray-200 w-5px h-2px"></span>
					</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">  Summary Report</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->
			
		</div>
		<!--end::Container-->
	</div>
	<!--end::Toolbar-->

	<div style="overflow: scroll !important;">
		<table id="mainTable" width="100%" border="0" align="center"
			cellpadding="0" cellspacing="0" class="txnf">
			<tr>
				<!-- <td colspan="5" align="left"><h2>Refund Captured</h2></td> -->
			</tr>
			<tr>
				<td colspan="5" align="left" valign="top">
					<div class="MerchBx">
					<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
					<!-- 	<div class="col-md-12">
							<div class="card ">
								<div class="card-header card-header-rose card-header-text">
									<div class="card-text">
										<h4 class="card-title">Summary Report</h4>
									</div>
								</div>
								<div class="card-body ">
									<div class="container"> -->
										
													<%-- <s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN' || #session.USER_TYPE.name()=='RESELLER'}"> --%>
											
											<form action="merchantSummaryWiseReport" class="box-content"
												method="post" id="merchantWiseReportt">
															<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
<!-- 													<div class="col-sm-3 col-lg-5"> -->
<!-- 														<label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant </label><br> -->
														

<%-- 															<s:select name="payId" --%>
<%-- 																class="form-control form-control-solid" id="merchant" --%>
<%-- 																headerKey="All" headerValue="ALL" list="merchantList" --%>
<%-- 																listKey="payId" listValue="businessName" --%>
<%-- 																autocomplete="off" data-control="select-2" /> --%>


													

<!-- 													</div> -->
													<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Merchant</span>
												</label>
												<s:select name="payId"
													class="form-select form-select-solid adminMerchants"
													id="merchant" headerKey="All" headerValue="ALL"
													list="merchantList" listKey="payId"
													listValue="businessName" autocomplete="off"
													data-control="select2" />
													</div>
											
													<div class="col-sm-3 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date</span>
												</label>
														
														
														<!--  <input type="date" id="dateFrom" name="dateFrom"
																class="input-control" autocomplete="off" readonly="true"> -->
														<input type="date" class="form-control form-control-solid" id="dateFrom" name="dateFrom">
														
													</div>

													<div class="col-sm-3 fv-row">
															<button type="button" class="btn btn-primary"
																onclick="summitdetail()" style="margin-top: 25px;">Submit</button>

														
													</div>
												</div>
												</div>
												</div>
												</div>
												</div>
																								
											</form>

											<%-- </s:if> --%>
										</div>
									</div>


								</div>
							</div>
						</div>
					</div>

				</td>
			</tr>
			<tr>
				<td colspan="5" align="left"><h2>&nbsp;</h2></td>
			</tr>
			<tr>
				<td align="left" style="padding: 10px;">
<!-- table here -->
	<div id="kt_content_container" class="container-xxl">
						<!-- 	<div style="overflow: scroll !important;"> -->
								<div class="row my-5">
									<div class="col">
										<div class="card">
											<div class="card-body">
  <div class="row g-9 mb-8 justify-content-end">
	<div class="col-lg-4 col-sm-12 col-md-6">
	  <select name="currency" data-control="select2" data-placeholder="Actions" id="actions11"
	   class="form-select form-select-solid actions" data-hide-search="true" onchange="myFunction();">
		<option value="">Actions</option>
		<option value="copy">Copy</option>
		<option value="csv" >CSV</option>
		 <option value="pdf">PDF</option>        
		<option value="print">Print</option> 
		
	  </select>
	</div>
	<div class="col-lg-4 col-sm-12 col-md-6">
												<div class="dropdown1">
													<button
														class="form-select form-select-solid actions dropbtn1">Customize
														Columns</button>
													<div class="dropdown-content1">
														<a class="toggle-vis" data-column="0">Pay Id</a>
														<a class="toggle-vis" data-column="1">Merchant</a>
														<a class="toggle-vis" data-column="2">Gross Transaction Amt</a>
														<a class="toggle-vis" data-column="3">Total Aggregator Commission Amt Payable(Including GST)</a>
														<a class="toggle-vis" data-column="4">Total Acquirer Commission Amt Payable(Including GST)</a>
														<a class="toggle-vis" data-column="5">Total Amt Payable to Merchant A/c</a>
														<a class="toggle-vis" data-column="6">Total Payout Nodal Account</a>
													</div>
												</div>
											</div>
  </div>
<div class="row g-9 mb-8">
	<div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
<table id="example" class="display table table-striped table-row-bordered gy-5 gs-7" style="width:100%">
        <thead>
            <tr class="fw-bold fs-6 text-gray-800">
                <th>PAY_ID</th>
                <th>Merchant</th>
                <th>Gross Transaction Amt</th>
                <th>Total Aggregator Commission Amt Payable(Including GST)</th>
                <th>Total Acquirer Commission Amt Payable(Including GST)</th>
                <!-- <th>ACQUIRER_TDR_SC</th>
                <th>ACQUIRER_GST</th> -->
                <!-- <th>RESELLER_CHARGES</th>
                <th>RESELLER_GST</th> -->
                
                <th>Total Amt Payable to Merchant A/c</th>
                <th>Total Payout Nodal Account</th>
                
                
                
            </tr>
        </thead>
        <tbody>
       	<s:if test="{transactionStatusBeans.size() > 0}">
       	<s:iterator value="transactionStatusBeans">
            <tr>
                <td><s:property value="PAY_ID"/></td>
                <td><s:property value="businessName"/></td>
                <td><s:property value="AMOUNT"/></td>
                <td><s:property value="PG_TDR_SC"/></td>
                <td><s:property value="PG_GST"/></td>
                <%-- <td><s:property value="ACQUIRER_TDR_SC"/></td>
                <td><s:property value="ACQUIRER_GST"/></td> --%>
                <%-- <td><s:property value="RESELLER_CHARGES"/></td>
                <td><s:property value="RESELLER_GST"/></td> --%>
                
                <td><s:property value="netSettleAmount"/></td>
                <td><s:property value="ACQUIRER_GST"/></td>
                
                
            </tr>
            </s:iterator>
           </s:if>
        </tbody>
       <tfoot >
        <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
               <!--  <td></td>
                <td></td> -->
                <!-- <td></td>
                <td></td> -->
                <td></td>
                <td></td>
                <td></td>
            </tr>
       </tfoot>
       
    </table>
</div>
</div>
				</td>
			</tr>

		</table>
	</div>
	<script type="text/javascript">
	
	debugger
	const dateFromm = '<%=(String)request.getAttribute("dateFrom")%>';
	if(dateFromm!='null'){
		 $("#dateFrom").val(dateFromm);
	}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script>
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
			if (x == 'print') {
				document.querySelector('.buttons-print').click();
			}
		}
	</script>
		<script type="text/javascript">
		$('a.toggle-vis').on('click', function(e) {
			e.preventDefault();
			table = $('#example').DataTable();
			// Get the column API object
			var column1 = table.column($(this).attr('data-column'));
			// Toggle the visibility
			column1.visible(!column1.visible());
			if ($(this)[0].classList[1] == 'activecustom') {
				$(this).removeClass('activecustom');
			} else {
				$(this).addClass('activecustom');
			}
		});
	</script>
	<style>
.dt-buttons {
	display: none;
}
</style>
</body>
</html>